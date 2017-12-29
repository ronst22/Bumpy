from flask import jsonify
from flask import request

from backend.app import app
from backend.app import mongo

from backend.handlers.users import *
from backend.handlers.accident import *

if __name__ == '__main__':
    app.run(debug=True,host="0.0.0.0", port=65432)
