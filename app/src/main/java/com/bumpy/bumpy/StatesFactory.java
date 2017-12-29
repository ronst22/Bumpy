package com.bumpy.bumpy;

/**
 * Created by ronst on 12/29/2017.
 */

public class StatesFactory {
    public enum STATES {
        AMBULANCE,
        POLICE,
        INVALID
    }

    public static IState GetState(STATES state)
    {
        switch (state)
        {
            case AMBULANCE:
            {
                return new AmbulanceQuestion();
            }
        }

        return null;
    }
}
