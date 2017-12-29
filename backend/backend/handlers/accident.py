from datetime import datetime
from bson.objectid import ObjectId

from flask import jsonify
from flask import request

from backend.app import app
from backend.app import mongo

@app.route('/v1/accident', methods=['GET'])
def get_all_accidents():
    accidents = mongo.db.accident
    output = []
    for accident in accidents.find():
        output.append(str(accident['_id']))
    return jsonify({'result' : output})

@app.route('/v1/accident/<acc_id>', methods=['GET'])
def get_accident(acc_id):
    accidents = mongo.db.accident
    accident = accidents.find_one({'_id' : ObjectId(acc_id)})
    if accident is not None:
        output = {
            'acc_date': accident['acc_date'],
            'user_name': accident['user_name'],
            'with_ambulance': accident['with_ambulance'],
            'with_police': accident['with_police'],
            'other_name': accident['other_name'],
            'other_personal_id': accident['other_personal_id'],
            'other_car_number': accident['other_car_number']}
    else:
        output = {'name' : 'error'}
    return jsonify({'result' : output})

@app.route('/v1/accident', methods=['POST'])
def add_accident():
    accidents = mongo.db.accident
    acc_date = datetime.now()
    user_name = request.json['user_name']
    with_ambulance = request.json['with_ambulance']
    with_police = request.json['with_police']
    other_name = request.json['other_name']
    other_personal_id = request.json['other_personal_id']
    other_car_number = request.json['other_car_number']

    acc_id = accidents.insert({\
        'acc_date': acc_date,
        'user_name': user_name,
        'with_ambulance': with_ambulance,
        'with_police': with_police,
        'other_name': other_name,
        'other_personal_id': other_personal_id,
        'other_car_number': other_car_number})

    new_accident= accidents.find_one({'_id': acc_id})
    output = {'accident_id' : new_accident['acc_date']}
    return jsonify({'result' : output})
