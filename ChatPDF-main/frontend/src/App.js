import React from 'react'
import ChatPanel from './ChatPanel'
import FileViewer from './FileViewer'

function App () {
  // sourceId is used to store the SOURCEID returned by the server after file upload, which will be used in the chat process
  // The disabled state is used to determine whether to disable the chat panel. The chat panel should be disabled when no file is uploaded
  const [sourceId, setSourceId] = React.useState(null)
  const [disabled, setDisabled] = React.useState(true)

  // After the file is successfully uploaded, the FileViewer component will call this function and pass in the SOURCEID returned by the server
  // This function does two things: one is to save the SOURCEID, and the other is to enable the chat panel (set disabled to false)
  const handleFileUploaded = (sourceId) => {
    setSourceId(sourceId)
    setDisabled(false)
  }

  // Render the main HTML structure, including two components, ChatPanel and FileViewer
  return (
    <div className="App" style={{ display: 'grid', gridTemplateColumns: '2fr 3fr', backgroundColor: 'rgb(242,245,250)' }}>
      <ChatPanel sourceId={sourceId} disabled={disabled} />
      <FileViewer onFileUploaded={handleFileUploaded} />
    </div>
  )
}

export default App
