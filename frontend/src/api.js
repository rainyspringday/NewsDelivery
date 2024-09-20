import axios from 'axios';

const API_URL = 'http://ec2-16-170-252-108.eu-north-1.compute.amazonaws.com:8080'; 

export const processString = async (input) => {
    try {
        
        const response = await axios.get(`${API_URL}/process-string`, {
            params:{
                input: input 
            }
        });

        return response.data;
    } catch (error) {
        console.error('Error processing string:', error);
        throw error;
    }
};

