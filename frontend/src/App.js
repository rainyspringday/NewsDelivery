import React, { useState } from 'react';
import { processString } from './api.js'; 

const App = () => {
    const [inputValue, setInputValue] = useState('');
    const [responseValue, setResponseValue] = useState('');

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
          
            const modifiedString = await processString(inputValue);
            setResponseValue(modifiedString);  
        } catch (error) {
            console.error('Error processing the input:', error);
        }
    };

    return (
        <div>
            <h1>Local News</h1>
            <h2>Time to process~20 seconds, openai api is very slow( </h2>
            <h3>New York-good test case; Dallas - valid city but no news; Non usa city - no news message</h3>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)} 
                    placeholder="Enter a string"
                />
                <button type="search">Submit</button>
            </form>

            
            {responseValue && <p>News: {responseValue}</p>}
        </div>
    );
};

export default App;