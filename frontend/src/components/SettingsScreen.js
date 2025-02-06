import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./SettingsScreenStyles.css";
import LoadingScreen from "./LoadingScreen";
import axiosInstance from "../api/axios";

const SettingsScreen = ({ onClose, initialMode, onSave }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedMode, setSelectedMode] = useState(location.state?.type || "survival");
    const queryParams = new URLSearchParams(location.search);
    const queryPort = queryParams.get("port") || "";

    const [port, setPort] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isPortValid, setIsPortValid] = useState(null);

    const { roomName = "Default Room", type = "survival" } = location.state || {};

    const handleClose = () => {
        navigate('/');
    };

    const handlePortChange = (event) => {
        setPort(event.target.value);
    };

    const handleSavePort = async () => {
        setIsLoading(true);
        const formData = new FormData();
        formData.append("data", JSON.stringify({ port : queryPort, changeport: port, mode: "" }));

        try {
            await axiosInstance.post("/settings/save", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            alert("Port settings saved successfully.");
            setIsLoading(false);
            window.location.href = "/";
        } catch (error) {
            console.error("Error saving port settings:", error);
            alert("Failed to save port settings.");
            setIsLoading(false);
        }
    };

    const handleSaveMode = async () => {
        setIsLoading(true);
        const formData = new FormData();
        formData.append("data", JSON.stringify({ port : queryPort, changeport: "", mode: selectedMode }));

        try {
            await axiosInstance.post("/settings/save", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            alert("Game mode saved successfully.");
            setIsLoading(false);
            window.location.href = "/";
        } catch (error) {
            console.error("Error saving game mode:", error);
            alert("Failed to save game mode.");
            setIsLoading(false);
        }
    };
    const [file, setFile] = useState(null); // 파일 상태 추가

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0]; // 선택한 파일 가져오기
        setFile(selectedFile);
    };

    const handleSaveVersion = async () => {
        if (!file) {
            alert("Please select a file before uploading.");
            return;
        }

        setIsLoading(true);
        const formData = new FormData();
        formData.append("file", file);
        formData.append("data", JSON.stringify({ port : queryPort, changeport: "", mode: "" }));

        try {
            await axiosInstance.post("/settings/save", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            alert("Version uploaded successfully.");
            setIsLoading(false);
            window.location.href = "/";
        } catch (error) {
            console.error("Error uploading version:", error);
            alert("Failed to upload version.");
            setIsLoading(false);
        }
    };
    const handleCheckDuplicate = async () => {
        if (!port) {
            alert('Please enter a port number first.');
            return;
        }

        // 입력 값이 숫자로만 이루어져 있는지 확인
        if (!/^\d+$/.test(port)) {
            alert('Port number must contain only digits.');
            return;
        }

        const portNumber = parseInt(port, 10);

        // 포트 번호가 유효 범위를 벗어난 경우
        if (portNumber < 0 || portNumber > 65535) {
            alert('Port number must be between 0 and 65535.');
            return;
        }

        try {
            const response = await axiosInstance.get(`/checkup`, {
                params: { port: portNumber },
            });

            if (response.data.validate === "true") {
                alert("Port number is available.");
                setIsPortValid(true);
            } else {
                alert("Port number is already in use. Please choose another one.");
                setIsPortValid(false);
            }
        } catch (error) {
            console.error("Error checking port:", error);
            alert("An error occurred while checking the port. Please try again.");
        }
    };


    return (
        <div className="settings-screen">
            {isLoading && <LoadingScreen duration={5}/>}
            <h2 className="settings-title">{roomName} Settings</h2>

            <div className="settings-group">
                <label className="settings-label">Port</label>
                <input
                    type="text"
                    className="settings-input"
                    placeholder={`Current Port : ${queryPort}`}
                    value={port}
                    onChange={handlePortChange}
                />
                <button className="settings-save-button-dup" onClick={handleCheckDuplicate}>Check Duplicate</button>
                <button className="settings-save-button" onClick={handleSavePort}>SAVE</button>
            </div>
            <div className="settings-group">
                <label className="settings-label">Game Mode</label>
                <div className="settings-radio-group">
                    <button
                        className={`mode-button ${selectedMode === "survival" ? "selected survival" : ""}`}
                        onClick={() => setSelectedMode("survival")}
                    >
                        Survival
                    </button>
                    <button
                        className={`mode-button ${selectedMode === "creative" ? "selected creative" : ""}`}
                        onClick={() => setSelectedMode("creative")}
                    >
                        Creative
                    </button>
                    <button
                        className={`mode-button ${selectedMode === "adventure" ? "selected adventure" : ""}`}
                        onClick={() => setSelectedMode("adventure")}
                    >
                        Adventure
                    </button>
                    <button className="settings-save-button" onClick={handleSaveMode}>SAVE</button>
                </div>
            </div>

            <div className="settings-group">
                <div className="version-info-container">
                    <label className="settings-label">Version Upload</label>
                </div>
                <input
                    type="file"
                    name="jarFile"
                    accept=".jar"
                    onChange={handleFileChange} // 파일 선택 핸들러 추가
                />
                <button className="settings-save-button" onClick={handleSaveVersion}>SAVE</button>
            </div>

            <button className="settings-close-button" onClick={handleClose}>Close</button>
        </div>
    );
};

export default SettingsScreen;