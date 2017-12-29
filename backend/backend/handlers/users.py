from flask import jsonify
from flask import request

from backend.app import app
from backend.app import mongo

@app.route('/v1/user', methods=['GET'])
def get_all_users():
    users = mongo.db.users
    output = []
    for user in users.find():
        output.append({'name' : user['name']})
    return jsonify({'result' : output})

@app.route('/v1/user/<name>', methods=['GET'])
def get_user(name):
    users = mongo.db.users
    user = users.find_one({'name' : name})
    if user is not None:
        output = {'name' : user['name'],
                  'car_number': user['car_number'],
                  'car_insurance': user['car_insurance'],
                  'user_personal_id': user['user_personal_id']}
    else:
        output = {'name' : 'error'}

    return jsonify({'result' : output})

@app.route('/v1/user', methods=['POST'])
def add_user():
    users = mongo.db.users
    name = request.json['name']
    car_number = request.json['car_number']
    car_insurance = request.json['car_insurance']
    user_personal_id = request.json['user_personal_id']
    user_id = users.insert({'name': name,
                            'car_number': car_number,
                            'car_insurance': car_insurance,
                            'user_personal_id': user_personal_id})
    new_user = users.find_one({'_id': user_id})
    output = {'name' : new_user['name']}
    return jsonify({'result' : output})
