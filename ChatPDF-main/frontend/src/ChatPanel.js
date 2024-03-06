import React, { Component } from 'react'
import { Button, Card, Input } from '@douyinfe/semi-ui'
import axios from 'axios'

class ChatPanel extends Component {

  constructor() {
    super()
    // Initialize state in constructor.
    // textvalue stores the value of the input box, which is the question being typed in the input box
    // messageList is a list of dialogues, storing the asked questions and answers returned by chatpdf
    // awaitingAnswer stores whether it is waiting for a reply.
    this.state = {
      textvalue: '',
      messageList: [],
      awaitingAnswer: false
    }
    // Create a React ref to refer to a DOM node. Used to scroll the message to the bottom when a new message comes
    this.messagesEndRef = React.createRef()
  }

  // This is an event handler function, which handles the value change event of the input box and updates the textvalue in the state.
  handleChange = (value) => {
    this.setState({ textvalue: value })
  }

  // This is an event handler function, it handles keyboard key events. When the pressed key is Enter and the shift key is not pressed, call the handleClick function to send a message.
  handleKeyUp = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      this.handleClick()
    }
  }

  // This is a function that handles click events, sends messages and gets replies. It first sends the user's question, then calls the API, gets the answer and returns to update the messageList.
  handleClick = () => {
    if (this.state.textvalue.trim() !== '') {
      const question = this.state.textvalue
      const source_id = this.props.sourceId

      this.setState(prevState => ({
        messageList: [...prevState.messageList, question],
        textvalue: '',
        awaitingAnswer: true
      }), this.scrollToBottom)  // Add scrollToBottom here

      axios.post('http://127.0.0.1:5000/ask-question', { source_id, question })
        .then((response) => {
          const answer = response.data.content
          this.setState(prevState => ({
            messageList: [...prevState.messageList, answer],
            awaitingAnswer: false
          }), this.scrollToBottom)
        })
    }
  }


  // This is a function that scrolls the scrollbar to the bottom of the dialogue box.
  scrollToBottom = () => {
    this.messagesEndRef.current.scrollTop = this.messagesEndRef.current.scrollHeight
  }

  render () {
    // If waiting for a reply or without source ID, disable the input box and send button.
    const disabled = this.state.awaitingAnswer || !this.props.sourceId

    return (
      <div style={{ display: 'flex', justifyContent: 'flex-end', paddingRight: 20, width: '100%' }}>
        <div style={{ width: '500px', marginRight: 20, marginTop: 20, backgroundColor: 'white', borderRadius: 10 }}>
          <Card
            className='ChatPanel'
            title='Chat with the publication'
            headerStyle={{ textAlign: 'left', padding: 20 }}
            bodyStyle={{ padding: 0 }}
            style={{ height: '88vh', overflow: 'hidden', marginBottom: 0, }}
            bordered={false}
          >
            <div ref={this.messagesEndRef} style={{ overflowY: 'scroll', height: 'calc(88vh - 56px - 100px)', padding: '20px' }}>
              {this.state.messageList.map((message, index) => (
                <div
                  style={{
                    padding: '1px 15px',
                    margin: '15px 0',
                    borderRadius: '10px',
                    backgroundColor: index % 2 === 0 ? 'lightblue' : 'rgb(42,98,241)',
                    wordWrap: 'break-word',
                    overflowWrap: 'break-word'
                  }}
                  key={index}
                >
                  <p
                    style={{
                      color: index % 2 === 0 ? 'black' : 'white',
                      textAlign: 'left',
                    }}
                  >{message}</p>
                </div>
              ))}
            </div>
            {this.state.awaitingAnswer && (
              <p style={{ textAlign: 'center', marginTop: 10 }}>Wait for answer...</p>
            )}
          </Card>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 0, backgroundColor: 'white', padding: 15, paddingTop: 22, borderBottomLeftRadius: 10, borderBottomRightRadius: 10, borderTop: '1px solid rgb(237,237,239)' }}>
            <Input
              value={this.state.textvalue}
              placeholder="Input your question"
              onChange={this.handleChange}
              onKeyUp={this.handleKeyUp}
              disabled={disabled}
              style={{
                flex: '1',
                marginRight: '8px',
                border: disabled ? '1px solid gray' : '1px solid rgb(42,98,241)',
                boxShadow: disabled ? '0 0 10px 1px gray' : '0 0 10px 1px rgb(42,98,241)',
              }}
            />
            <Button
              theme='solid'
              type='primary'
              onClick={this.handleClick}
              disabled={disabled}
            >Send</Button>
          </div>
        </div>
      </div>
    )
  }
}

export default ChatPanel
