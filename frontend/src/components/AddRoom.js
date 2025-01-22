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
    const [isPortValid, setIsPortValid] = useState(null); // Null: 확인 안 됨, true: 사용 가능, false: 중복

    const handleCheckDuplicate = async () => {
        if (!formState.port) {
            alert('Please enter a port number first.');
            return;
        }

        // 중복 확인 로직 (예: API 호출 대신 가상 데이터 사용)
        const duplicatePorts = [25565, 25566, 25567]; // 중복된 포트 예제
        const isDuplicate = duplicatePorts.includes(parseInt(formState.port, 10));

        setIsPortValid(!isDuplicate);

        if (isDuplicate) {
            alert('Port number is already in use. Please choose another one.');
        } else {
            alert('Port number is available.');
        }
    };

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
                    <button
                        type="button"
                        className="check-button-dup"
                        onClick={handleCheckDuplicate}
                    >
                        Check Duplicate
                    </button>
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
