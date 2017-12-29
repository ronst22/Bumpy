from io import open

from setuptools import find_packages, setup

with open('requirements.txt') as f:
    required = [r for r in f.read().splitlines() if not r.startswith('git+http')]

setup(
    name='backend',
    version="1.0.0",
    description='',
    long_description="The backend of the bumpy application",
    url='https://github.com/tests/tests',
    install_requires=required,
    packages=find_packages(),
)
