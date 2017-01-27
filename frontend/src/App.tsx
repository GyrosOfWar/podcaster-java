import * as React from 'react';
import './App.css';

import Login from "./auth/Login";

class App extends React.Component<null, null> {
  render() {
    return (
      <div className="App">
        <Login />
      </div>
    );
  }
}

export default App;
