import React, { useState } from 'react';
import './AddRoomStyle.css';
import axiosInstance from "../api/axios";

const AddRoom = ({ onClose, onCreateRoom }) => {
    const [formState, setFormState] = useState({
        mapName: '',
        port: '',
        gameMode: 'Survival',
        jarFile: null,
        minHeap: '',
        maxHeap: '',
    });
    const [isPortValid, setIsPortValid] = useState(null);

    const handleCheckDuplicate = async () => {
        if (!formState.port) {
            alert('Please enter a port number first.');
            return;
        }

        // 입력 값이 숫자로만 이루어져 있는지 확인
        if (!/^\d+$/.test(formState.port)) {
            alert('Port number must contain only digits.');
            return;
        }

        const portNumber = parseInt(formState.port, 10);

        // 포트 번호가 유효 범위를 벗어난 경우
        if (portNumber < 0 || portNumber > 65535) {
            alert('Port number must be between 0 and 65535.');
            return;
        }

        try {
            // GET 요청을 통해 백엔드로 포트 확인 요청
            const response = await axiosInstance.get(`/checkup`, {
                params: { port: portNumber }, // 쿼리 파라미터로 포트 번호 전달
            });

            // 서버 응답 처리
            if (response.data.validate === "true") {
                alert('Port number is available.');
                setIsPortValid(true); // 포트 유효 상태 업데이트
            } else {
                alert('Port number is already in use. Please choose another one.');
                setIsPortValid(false); // 포트 유효 상태 업데이트
            }
        } catch (error) {
            console.error("Error checking port:", error);
            alert("An error occurred while checking the port. Please try again.");
        }
    };


    const handleModeSelect = (mode) => {
        setFormState({ ...formState, gameMode: mode });
    };

    const handleChange = (e) => {
        const { name, value, files } = e.target;

        if (name === 'port' || name === 'minHeap' || name === 'maxHeap') {
            if (!/^\d*$/.test(value)) {
                alert('Only numeric values are allowed.');
                return;
            }
        }

        setFormState({
            ...formState,
            [name]: files ? files[0] : value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 필수 검증 로직
        if (!formState.gameMode) {
            alert('Please select a game mode.');
            return;
        }

        if (isPortValid === null) {
            alert('Please check port availability before creating the room.');
            return;
        }

        if (!isPortValid) {
            alert('The selected port number is invalid. Please choose a valid port.');
            return;
        }

        if (parseInt(formState.minHeap, 10) > parseInt(formState.maxHeap, 10)) {
            alert('Min Heap Memory cannot be greater than Max Heap Memory.');
            return;
        }

        if (formState.port === '' || formState.minHeap === '' || formState.maxHeap === '') {
            alert('Please fill out all required fields.');
            return;
        }

        // FormData 객체 생성 및 데이터 추가
        const formData = new FormData();
        formData.append("file", formState.jarFile); // JAR 파일
        formData.append(
            "data",
            JSON.stringify({
                port: formState.port,
                name: formState.mapName,
                mode: formState.gameMode,
                xmx: formState.maxHeap,
                xms: formState.minHeap,
            })
        );

        try {
            // POST 요청
            const response = await axiosInstance.post(`/addroom`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data", // 멀티파트 데이터 형식 지정
                },
            });

            // 서버 응답 처리
            if (response.status === 200) {
                alert("Room created successfully!");
                onCreateRoom(formState); // 상위 컴포넌트에 알림
                onClose(); // 팝업 닫기
            } else {
                alert("Failed to create room. Please try again.");
            }
        } catch (error) {
            console.error("Error creating room:", error);
            alert("An error occurred while creating the room. Please try again.");
        }
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
                        <button type="button" onClick={onClose}>Cancel</button>
                        <button type="submit">Create Room</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddRoom;
