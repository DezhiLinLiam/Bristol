import requests

# Fill in your API key. The key can be obtained from the chatpdf website.
api_key = 'sec_evbmouCpg9Ny0B0Q8BuzG3CeSaqGtoT2'


# The add_pdf_file function is used to add a PDF file to the ChatPDF API.
def add_pdf_file(file):
    headers = {
        'x-api-key': api_key,
    }

    # Create a dictionary containing the file content, to be used as the file data part of the request.
    # The key of the dictionary is 'file', which should be the same as the key added to the FormData object in the frontend code.
    # The value of the dictionary is a tuple, containing two elements: filename and file content. Both values can be obtained from the passed file object.
    files = {'file': (file.filename, file.stream.read())}

    # Use the requests library to send a POST request, uploading the PDF file to the ChatPDF API.
    response = requests.post('https://api.chatpdf.com/v1/sources/add-file', headers=headers, files=files)

    # The response body is in JSON format, we parse it and return.
    return response.json()


# The ask_question function is used to send questions to the ChatPDF API and get answers.
def ask_question(source_id, question):
    headers = {
        'x-api-key': api_key,
        'Content-Type': 'application/json',
    }

    # Create a dictionary containing the data sent to the ChatPDF API.
    # It includes sourceId and messages. Messages is a list, each element in the list is a dictionary, representing a message.
    data = {
        'sourceId': source_id,
        'messages': [
            {
                'role': "user",
                'content': question
            }
        ]
    }

    # Use the requests library to send a POST request, send the question to the ChatPDF API, and get the answer.
    response = requests.post('https://api.chatpdf.com/v1/chats/message', headers=headers, json=data)

    return response.json()
