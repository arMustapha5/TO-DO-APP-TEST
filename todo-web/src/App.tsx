import React, { useState, useEffect } from 'react';
import toast, { Toaster } from 'react-hot-toast';
import './App.css';

interface Todo {
  id: number;
  text: string;
  completed: boolean;
}

interface User {
  username: string;
  token: string;
}

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [todos, setTodos] = useState<Todo[]>([]);
  const [newTodo, setNewTodo] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editText, setEditText] = useState('');
  const [loginError, setLoginError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');
    if (token && storedUsername) {
      setUser({ username: storedUsername, token });
      fetchTodos(token);
    }
  }, []);

  const login = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    
    try {
      const response = await fetch('http://localhost:3001/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      
      const data = await response.json();
      
      if (response.ok) {
        const userData = { username, token: data.token };
        setUser(userData);
        localStorage.setItem('token', data.token);
        localStorage.setItem('username', username);
        fetchTodos(data.token);
        setUsername('');
        setPassword('');
        toast.success(`Welcome back, ${username}!`);
      } else {
        const errorMessage = data.message || 'Login failed';
        setLoginError(errorMessage);
        toast.error(errorMessage);
      }
    } catch (error) {
      const errorMessage = 'Network error';
      setLoginError(errorMessage);
      toast.error(errorMessage);
    }
  };

  const logout = () => {
    setUser(null);
    setTodos([]);
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    toast.success('Logged out successfully');
  };

  const fetchTodos = async (token: string) => {
    try {
      const response = await fetch('http://localhost:3001/items', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setTodos(data);
      } else {
        toast.error('Failed to fetch todos');
      }
    } catch (error) {
      console.error('Error fetching todos:', error);
      toast.error('Failed to fetch todos');
    }
  };

  const addTodo = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTodo.trim() || !user) return;

    try {
      const response = await fetch('http://localhost:3001/items', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${user.token}`
        },
        body: JSON.stringify({ text: newTodo })
      });

      if (response.ok) {
        const todo = await response.json();
        setTodos([...todos, todo]);
        setNewTodo('');
        toast.success('Todo added successfully');
      } else {
        toast.error('Failed to add todo');
      }
    } catch (error) {
      console.error('Error adding todo:', error);
      toast.error('Failed to add todo');
    }
  };

  const updateTodo = async (id: number, text: string) => {
    if (!user) return;

    try {
      const response = await fetch(`http://localhost:3001/items/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${user.token}`
        },
        body: JSON.stringify({ text })
      });

      if (response.ok) {
        const updatedTodo = await response.json();
        setTodos(todos.map(todo => todo.id === id ? updatedTodo : todo));
        setEditingId(null);
        setEditText('');
        toast.success('Todo updated successfully');
      } else {
        toast.error('Failed to update todo');
      }
    } catch (error) {
      console.error('Error updating todo:', error);
      toast.error('Failed to update todo');
    }
  };

  const deleteTodo = async (id: number) => {
    if (!user) return;

    try {
      const response = await fetch(`http://localhost:3001/items/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${user.token}` }
      });

      if (response.ok) {
        setTodos(todos.filter(todo => todo.id !== id));
        toast.success('Todo deleted successfully');
      } else {
        toast.error('Failed to delete todo');
      }
    } catch (error) {
      console.error('Error deleting todo:', error);
      toast.error('Failed to delete todo');
    }
  };

  const startEditing = (id: number, text: string) => {
    setEditingId(id);
    setEditText(text);
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditText('');
  };

  if (!user) {
    return (
      <div className="App">
        <div className="login-container">
          <h1>Todo App</h1>
          <form onSubmit={login} className="login-form">
            <div className="form-group">
              <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                data-testid="username-input"
                required
              />
            </div>
            <div className="form-group">
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                data-testid="password-input"
                required
              />
            </div>
            <button type="submit" data-testid="login-button">Login</button>
            {loginError && <div className="error" data-testid="login-error">{loginError}</div>}
          </form>
          <div className="demo-credentials">
            <p><strong>Demo credentials:</strong></p>
            <p>Username: admin, Password: password</p>
            <p>Username: user, Password: 123456</p>
          </div>
        </div>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#333',
              color: '#fff',
            },
            success: {
              style: {
                background: '#10B981',
              },
            },
            error: {
              style: {
                background: '#EF4444',
              },
            },
          }}
        />
      </div>
    );
  }

  return (
    <div className="App">
      <div className="app-container">
        <header className="app-header">
          <h1>Todo App</h1>
          <div className="user-info">
            <span>Welcome, {user.username}</span>
            <button onClick={logout} data-testid="logout-button">Logout</button>
          </div>
        </header>

        <form onSubmit={addTodo} className="add-todo-form">
          <input
            type="text"
            placeholder="Add new todo..."
            value={newTodo}
            onChange={(e) => setNewTodo(e.target.value)}
            data-testid="new-todo-input"
          />
          <button type="submit" data-testid="add-todo-button">Add</button>
        </form>

        <div className="todos-container">
          {todos.length === 0 ? (
            <p className="no-todos" data-testid="no-todos">No todos yet. Add one above!</p>
          ) : (
            <ul className="todos-list">
              {todos.map(todo => (
                <li key={todo.id} className="todo-item" data-testid={`todo-${todo.id}`}>
                  {editingId === todo.id ? (
                    <div className="edit-todo">
                      <input
                        type="text"
                        value={editText}
                        onChange={(e) => setEditText(e.target.value)}
                        data-testid={`edit-input-${todo.id}`}
                      />
                      <button 
                        onClick={() => updateTodo(todo.id, editText)}
                        data-testid={`save-button-${todo.id}`}
                      >
                        Save
                      </button>
                      <button 
                        onClick={cancelEditing}
                        data-testid={`cancel-button-${todo.id}`}
                      >
                        Cancel
                      </button>
                    </div>
                  ) : (
                    <div className="todo-content">
                      <span className="todo-text" data-testid={`todo-text-${todo.id}`}>
                        {todo.text}
                      </span>
                      <div className="todo-actions">
                        <button 
                          onClick={() => startEditing(todo.id, todo.text)}
                          data-testid={`edit-button-${todo.id}`}
                        >
                          Edit
                        </button>
                        <button 
                          onClick={() => deleteTodo(todo.id)}
                          data-testid={`delete-button-${todo.id}`}
                          className="delete-button"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 3000,
          style: {
            background: '#333',
            color: '#fff',
          },
          success: {
            style: {
              background: '#10B981',
            },
          },
          error: {
            style: {
              background: '#EF4444',
            },
          },
        }}
      />
    </div>
  );
}

export default App;
