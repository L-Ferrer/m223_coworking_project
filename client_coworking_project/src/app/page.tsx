"use client"
import  { useState } from 'react';
import reportWebVitals from '../scripts/reportWebVitals';

export default function Home() {
  const [bearerToken, setBearerToken] = useState('');
  const [tasks, setTasks] = useState<Task[] | null>(null);
  const [user, setUser] = useState<User | null>(null);

reportWebVitals(console.log());

  interface Task {
    id: number,
    description: string,
    user: User
  }
  interface User{
    id: number,
    username: string,
    password: string,
    role: string,
    salt: string
  }

  // Fetch the Bearer token from the API and store it in state
  const fetchBearerToken = async () => {
    try {
      const response = await fetch('http://localhost:8080/login', {
        method: 'POST',
        // Include any necessary authentication data in the request body
        body: JSON.stringify({ username: 'admin', password: '123' }),
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }

      });
      // from the response-body
      if (response.ok) {
        const data = await response.json();
        const token = data.token; // Assuming the token is returned in the response data
        setBearerToken(`Bearer ${token}`); // Store the token with the 'Bearer ' prefix in state
      } else {
        console.error('Failed to fetch Bearer token');
      }
    } catch (error) {
      console.error('Error fetching Bearer token:', error);
    }
  };


  const fetchAllTasksWithToken = async () => {
    try {
      const response = await fetch('http://localhost:8080/users/2/tasks', {
        method: 'GET',
        // include data for authentication
        // body: none
        headers: {
          'Authorization': bearerToken,
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      if (response.ok) {
        const data = await response.json()
        setTasks(data)
        setUser(data[0].user)
      } else {
        console.log('failed to fetch data')
      }
    } catch (error) {
      console.error('Error fetching data: ', error)
    }
  }


  return (
    <main>
      <div>
        {/* Render your components and use the 'bearerToken' state as needed */}
        <button onClick={fetchBearerToken}>Fetch Bearer Token</button>
        <p>Bearer Token: {bearerToken}</p>
        <p>User : {user?.username}</p>


        <button onClick={fetchAllTasksWithToken}>fetch all tasks from user n</button>
        <ul>
          {
            tasks?.map(t => <li key={t.id}>{t.description}</li>)
          }

        </ul>
        {/*<p>tasks : {tasks}</p>*/}
      </div>
    </main>
  )
}
