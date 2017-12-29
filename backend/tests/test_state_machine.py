import pytest

from dn_common.state_machine import StateMachine, State, RunResult


class DummyState1(State):

    def run(self, param)->RunResult:
        return RunResult.CONTINUE

    def get_next(self)->State:
        return DummyState2(self._ctx)


class DummyState2(State):

    def run(self, param)->RunResult:
        if self._ctx.get('cnt', 0) == 10:
            return RunResult.STOP
        self._ctx['cnt'] = self._ctx.get('cnt', 0) + 1
        return RunResult.CONTINUE

    def get_next(self)->State:
        return DummyState1(self._ctx)


class DummyStateMachine(StateMachine):

    def __init__(self, ctx):
        self._ctx = ctx
        super(DummyStateMachine, self).__init__()

    @property
    def _initial_state(self)->State:
        return DummyState1(self._ctx)


def test_block_direct_creation():
    with pytest.raises(TypeError):
        StateMachine()
    with pytest.raises(TypeError):
        State()


def test_state_machine():
    ctx = {}
    sm = DummyStateMachine(ctx)
    sm.run(1)
    assert ctx['cnt'] == 10
