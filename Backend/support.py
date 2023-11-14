from fastapi import FastAPI, UploadFile
import pyodbc
import os
from PIL import Image
from roboflow import Roboflow
import io


def get_animal_info(animal_name):
    cnxn = get_db_connection()
    if cnxn:
        try:            
            cursor = cnxn.cursor()
            cursor.execute("EXEC ws.spGetAnimalInfo ?", animal_name)            
            record = cursor.fetchone()
            cnxn.commit()
            cursor.close()
            cnxn.close()
            
            if record:
                return {
                    "shortDescription": record[0],
                    "habitat": record[1],
                    "diet": record[2],
                    "location": record[3],
                    "type": record[4],
                    "lifeSpan": record[5],
                    "weight": record[6],
                    "top_speed": record[7]
                }
            else:
                return None
        except Exception as e:
            print(f"An error occurred: {e}")
            return str(e)
    else:
        print("Connection string not found in environment variables.")
        return None


def get_random_fact():
    conn = get_db_connection()
    
    if conn:
        try:
            cursor = conn.cursor()
            cursor.execute("EXEC ws.spRandomFact")
            record = cursor.fetchone()
            conn.commit()
            cursor.close()
            conn.close()
            if record:
                return {
                    "fact": record[1],
                    "primaryImage": record[2],
                    "secondaryImage": record[3]
                }
            else:
                return None
        except Exception as e:
            print(f"An error occurred: {e}")
            return str(e)
    else:
        print("Connection string not found in environment variables.")
        return None

def classify_image_from_data(image_data, model):
    with open("temp_image.jpg", "wb") as f:
        f.write(image_data)

    full_prediction = model.predict("temp_image.jpg", confidence=40, overlap=30).json()

    if 'predictions' in full_prediction and full_prediction['predictions']:
        primary_prediction = full_prediction["predictions"][0]
        simplified_prediction = {
            "class": primary_prediction["class"],
            "confidence": round(primary_prediction["confidence"], 3)
        }
        return {"prediction": simplified_prediction}
    else:
        return {"error": "No predictions found in the response"}
    
def get_db_connection():
    conn = pyodbc.connect(os.getenv('WSconnectionString'))
    return conn