import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./SettingsScreenStyles.css";
import LoadingScreen from "./LoadingScreen";

const SettingsScreen = ({ onClose, initialMode, onSave }) => {
    const navigate = useNavigate();
    const [selectedMode, setSelectedMode] = useState(initialMode || "Survival");
    const [port, setPort] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleModeChange = (event) => {
        setSelectedMode(event.target.value);
    };

    const handlePortChange = (event) => {
        setPort(event.target.value);
    };

    const handleSave = () => {
        setIsLoading(true);
        setTimeout(() => {
            setIsLoading(false);
            navigate('/');
        }, 5000);
    };

    return (
        <div className="settings-screen">
            {isLoading && <LoadingScreen duration={5}/>}
            <h2 className="settings-title">RoomNameTemp Settings</h2>

            <div className="settings-group">
                <label className="settings-label">Port</label>
                <input
                    type="text" 
                    className="settings-input"
                    placeholder="Enter Port"
                    value={port}
                    onChange={handlePortChange}
                />
                <button className="settings-save-button" onClick={handleSave}>SAVE</button>
            </div>
            <div className="settings-group">
                <label className="settings-label">Game Mode</label>
                <div className="settings-radio-group">
                    <button
                        className={`mode-button ${selectedMode === "Survival" ? "selected survival" : ""}`}
                        onClick={() => setSelectedMode("Survival")}
                    >
                        Survival
                    </button>
                    <button
                        className={`mode-button ${selectedMode === "Creative" ? "selected creative" : ""}`}
                        onClick={() => setSelectedMode("Creative")}
                    >
                        Creative
                    </button>
                    <button
                        className={`mode-button ${selectedMode === "Adventure" ? "selected adventure" : ""}`}
                        onClick={() => setSelectedMode("Adventure")}
                    >
                        Adventure
                    </button>
                    <button className="settings-save-button" onClick={handleSave}>SAVE</button>
                </div>
            </div>

            <div className="settings-group">
                <div className="version-info-container">
                    <label className="settings-label">Version Upload</label>
                    <p className="version-info">Current Ver - 1.12.1</p>
                </div>
                <button className="jar-upload-button">JAR Upload</button>
                <button className="settings-save-button" onClick={handleSave}>SAVE</button>
            </div>

            <button className="settings-close-button" onClick={onClose}>Close</button>
        </div>
    );
};

export default SettingsScreen;