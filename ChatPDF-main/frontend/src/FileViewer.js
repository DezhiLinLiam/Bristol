import React, { Component } from 'react'
import { Button } from '@douyinfe/semi-ui'
import axios from 'axios'


class FileViewer extends Component {
  fileInputRef = React.createRef();

  constructor(props) {
    super(props)
    this.state = {
      file: null, // Used to save the URL of the file selected from the file input element
      selectedFile: null, // Used to save the selected file
      isFileSelected: false, // Used to indicate whether a file has been selected
    }
  }

  // Define a function that is called when the value of the file input element changes
  // This function gets the selected file from the event object and updates the component's state
  // Then it calls the handleFileUpload function to start uploading the file
  handleFileChange = (event) => {
    const selectedFile = event.target.files[0]
    this.setState(
      {
        file: URL.createObjectURL(selectedFile), // Create an object URL and save it to the state
        selectedFile: selectedFile, // Save the selected file
        isFileSelected: true, // Set isFileSelected to true, indicating that a file has been selected, so the input box and button can be used
      },
      this.handleFileUpload
    )
  };

  // Define a function for uploading files
  // This function creates a new instance of FormData and adds the selected file
  // It then uses axios to send a POST request to the server and handle the response or error
  handleFileUpload = () => {
    const data = new FormData()
    data.append('file', this.state.selectedFile)

    axios
      .post('http://127.0.0.1:5000/add-pdf-file', data)
      .then((response) => {
        const sourceId = response.data.sourceId // Get the sourceId from the response
        console.log('File upload successful. Received sourceId:', sourceId)
        // Call the onFileUploaded callback function passed by the parent component and pass the sourceId as an argument
        this.props.onFileUploaded(sourceId)
      })
      .catch((error) => {
        if (error.response) {
          console.error('File upload failed:', error.response.data, error.response.status)
        } else if (error.request) {
          console.error('File upload failed, no response:', error.request)
        } else {
          console.error('File upload failed, other error:', error.message)
        }
      })
  };

  // Define a function that is called when the user clicks the "Select File" button
  // This function triggers the click event of the file input element, thereby opening the file selection dialog
  handleButtonClick = () => {
    this.fileInputRef.current.click()
  };

  render () {
    return (
      <div className="FileViewer" style={{ backgroundColor: 'white', margin: 20, height: '95vh', width: 900, position: 'relative' }}>
        <input
          type="file"
          onChange={this.handleFileChange}
          ref={this.fileInputRef}
          style={{ display: 'none' }}
        />
        {!this.state.isFileSelected && (
          <Button
            theme="solid"
            type="primary"
            onClick={this.handleButtonClick}
            style={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 1,
            }}
          >
            Select File
          </Button>
        )}
        {this.state.file && <iframe src={this.state.file} style={{ width: '100%', height: '100%', border: 'none' }} />}
      </div>
    )
  }
}

export default FileViewer
