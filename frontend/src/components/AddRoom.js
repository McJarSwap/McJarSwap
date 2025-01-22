import React, { useState } from 'react';
import './AddRoomStyle.css';

const AddRoom = ({ onClose, onCreateRoom }) => {
    const [formState, setFormState] = useState({
        mapName: '',
        port: '',
        gameMode: 'Survival', // 선택된 모드 저장
        jarFile: null,
        minHeap: '',
        maxHeap: '',
    });

    const handleModeSelect = (mode) => {
        setFormState({ ...formState, gameMode: mode });
    };

    const handleChange = (e) => {
        const { name, value, files } = e.target;
        setFormState({
            ...formState,
            [name]: files ? files[0] : value,
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!formState.gameMode) {
            alert('Please select a game mode.');
            return;
        }
        onCreateRoom(formState);
    };

    return (
        <div className="popup-overlay">
            <div className="popup-content">
                <h2>Create Room</h2>
                <form className="room-form" onSubmit={handleSubmit}>
                    <label>
                        Map Name
                        <input
                            type="text"
                            name="mapName"
                            placeholder="Enter map name"
                            value={formState.mapName}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <label>
                        Port Number
                        <input
                            type="number"
                            name="port"
                            placeholder="Enter port number"
                            value={formState.port}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <div className="game-mode-buttons">
                        <h3>Select Game Mode</h3>
                        {['Survival', 'Creative', 'Adventure'].map((mode) => (
                            <button
                                type="button"
                                key={mode}
                                className={`mode-button ${mode.toLowerCase()} ${
                                    formState.gameMode === mode ? 'selected' : ''
                                }`}
                                onClick={() => handleModeSelect(mode)}
                            >
                                {mode}
                            </button>
                        ))}
                    </div>
                    <br></br>
                    <label>
                        Server JAR File
                        <input
                            type="file"
                            name="jarFile"
                            accept=".jar"
                            onChange={handleChange}
                        />
                    </label>
                    <label>
                        Min Heap Memory
                        <input
                            type="number"
                            name="minHeap"
                            placeholder="Enter min heap memory (MB)"
                            value={formState.minHeap}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <label>
                        Max Heap Memory
                        <input
                            type="number"
                            name="maxHeap"
                            placeholder="Enter max heap memory (MB)"
                            value={formState.maxHeap}
                            onChange={handleChange}
                            required
                        />
                    </label>
                    <div className="form-actions">
                        <button type="submit">Create Room</button>
                        <button type="button" onClick={onClose}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddRoom;
