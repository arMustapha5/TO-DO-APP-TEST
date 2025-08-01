const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

const app = express();
const PORT = process.env.PORT || 3001;
const JWT_SECRET = 'your-secret-key';

app.use(cors());
app.use(express.json());

const users = [
    { id: 1, username: 'admin', password: bcrypt.hashSync('password', 10) },
    { id: 2, username: 'user', password: bcrypt.hashSync('123456', 10) }
];

let todos = [];
let nextTodoId = 1;

const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ message: 'Access token required' });
    }

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            return res.status(403).json({ message: 'Invalid or expired token' });
        }
        req.user = user;
        next();
    });
};

app.post('/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        if (!username || !password) {
            return res.status(400).json({ message: 'Username and password are required' });
        }

        const user = users.find(u => u.username === username);
        if (!user) {
            return res.status(401).json({ message: 'Invalid credentials' });
        }

        const isValidPassword = await bcrypt.compare(password, user.password);
        if (!isValidPassword) {
            return res.status(401).json({ message: 'Invalid credentials' });
        }

        const token = jwt.sign(
            { id: user.id, username: user.username },
            JWT_SECRET,
            { expiresIn: '24h' }
        );

        res.json({ token, user: { id: user.id, username: user.username } });
    } catch (error) {
        res.status(500).json({ message: 'Server error' });
    }
});

app.get('/items', authenticateToken, (req, res) => {
    const userTodos = todos.filter(todo => todo.userId === req.user.id);
    res.json(userTodos);
});

app.post('/items', authenticateToken, (req, res) => {
    try {
        const { text } = req.body;

        if (!text || text.trim() === '') {
            return res.status(400).json({ message: 'Todo text is required' });
        }

        const newTodo = {
            id: nextTodoId++,
            text: text.trim(),
            completed: false,
            userId: req.user.id,
            createdAt: new Date().toISOString()
        };

        todos.push(newTodo);
        res.status(201).json(newTodo);
    } catch (error) {
        res.status(500).json({ message: 'Server error' });
    }
});

app.put('/items/:id', authenticateToken, (req, res) => {
    try {
        const todoId = parseInt(req.params.id);
        const { text, completed } = req.body;

        const todoIndex = todos.findIndex(todo =>
            todo.id === todoId && todo.userId === req.user.id
        );

        if (todoIndex === -1) {
            return res.status(404).json({ message: 'Todo not found' });
        }

        if (text !== undefined) {
            if (!text || text.trim() === '') {
                return res.status(400).json({ message: 'Todo text cannot be empty' });
            }
            todos[todoIndex].text = text.trim();
        }

        if (completed !== undefined) {
            todos[todoIndex].completed = completed;
        }

        todos[todoIndex].updatedAt = new Date().toISOString();

        res.json(todos[todoIndex]);
    } catch (error) {
        res.status(500).json({ message: 'Server error' });
    }
});

app.delete('/items/:id', authenticateToken, (req, res) => {
    try {
        const todoId = parseInt(req.params.id);

        const todoIndex = todos.findIndex(todo =>
            todo.id === todoId && todo.userId === req.user.id
        );

        if (todoIndex === -1) {
            return res.status(404).json({ message: 'Todo not found' });
        }

        todos.splice(todoIndex, 1);
        res.status(204).send();
    } catch (error) {
        res.status(500).json({ message: 'Server error' });
    }
});

app.get('/health', (req, res) => {
    res.json({ status: 'OK', timestamp: new Date().toISOString() });
});

app.delete('/test/clear-data', (req, res) => {
    todos = [];
    nextTodoId = 1;
    res.json({ message: 'Test data cleared', timestamp: new Date().toISOString() });
});

app.use((req, res) => {
    res.status(404).json({ message: 'Endpoint not found' });
});

app.use((err, req, res, next) => {
    if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
        return res.status(400).json({ message: 'Invalid JSON' });
    }
    console.error(err.stack);
    res.status(500).json({ message: 'Something went wrong!' });
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    console.log(`Demo users:`);
    console.log(`- Username: admin, Password: password`);
    console.log(`- Username: user, Password: 123456`);
});

module.exports = app;
