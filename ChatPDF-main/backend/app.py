from flask import Flask, jsonify, request
from api import add_pdf_file, ask_question
from flask_cors import CORS

app = Flask(__name__)

# Allow cross-domain requests for all URLs
CORS(app, resources={r"/*": {"origins": "*"}})


# Define the root route, responds to GET requests,
# returns a JSON message for server response testing
@app.route('/', methods=['GET'])
def home():
    return jsonify({'message': 'Server is running'}), 200


# Define /test-get route, responds to GET requests,
# returns a JSON message for testing if the server's GET function is working properly
@app.route('/test-get', methods=['GET'])
def handle_test_get():
    app.logger.info('Received GET request to /test-get')
    return jsonify({'message': 'GET request successful'}), 200


# Define /add-pdf-file route, responds to POST requests,
# handles uploaded PDF files
@app.route('/add-pdf-file', methods=['POST'])
def handle_add_pdf_file():
    app.logger.info('Received request to /add-pdf-file')

    # Check if the request contains a file
    if 'file' not in request.files:
        app.logger.error('No file part in the request')
        return jsonify({'message': 'No file part in the request'}), 400

    # Get the file from the request
    file = request.files['file']

    # Check if the fetched file is empty
    if file is None:
        app.logger.error('No file content in the request')
        return jsonify({'message': 'No file content in the request'}), 400

    # Call the add_pdf_file function to process the file
    # and get the returned result
    result = add_pdf_file(file)
    app.logger.info('File uploaded successful. Returned data:', result)

    # Return the result to the client
    return jsonify(result)


# Define /ask-question route, responds to POST requests,
# handles the questions sent by the client
@app.route('/ask-question', methods=['POST'])
def handle_ask_question():
    app.logger.info('Received request to /ask-question')

    # Get JSON data from the request
    data = request.get_json()

    # Get source_id and question from the data
    source_id = data.get('source_id')
    question = data.get('question')
    print('Received source_id:', source_id)
    print('Received question:', question)

    # Check if the data is empty
    if data is None:
        app.logger.error('No data in the request')
        return jsonify({'message': 'No data in the request'}), 400

    # Re-get source_id and question from the data
    source_id = data.get('source_id')
    question = data.get('question')

    # Check if both source_id and question exist
    if not all([source_id, question]):
        app.logger.error('Missing data in the request')
        return jsonify({'message': 'Missing data in the request'}), 400

    # Call the ask_question function to process the question
    # and get the returned result
    result = ask_question(source_id, question)  # api_key removed
    app.logger.info('Question asked successful. Returned data:', result)

    # Return the result to the client
    return jsonify(result)


# Check if the current program is running as the main program,
# if yes, start the Flask server
if __name__ == "__main__":
    app.run(host='localhost', port=5000, debug=True)
