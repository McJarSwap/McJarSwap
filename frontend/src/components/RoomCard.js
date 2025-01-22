// RoomCard.js
import React from "react";
import { useNavigate } from "react-router-dom";
import "./RoomCardStyles.css";
import TrashIcon from "../icons/TrashIcon.svg";

const RoomCard = ({ roomName, port, type, onDelete }) => {
    const navigate = useNavigate();

    const openSettingsPage = () => {
        navigate(`/settings?port=${port}`, { state: { roomName, port, type } });
    };

    const typeColor =
        type === "Survival" ? "red" :
            type === "Creative" ? "blue" :
                type === "Adventure" ? "green" : "default";
    return (
        <div className="room-card">
            <div className="room-card-row">
                <span className="room-info">{roomName}</span>
                <span className="port-info">Port: {port}</span>
                <button className="delete-button" onClick={onDelete}>
                    <img src={TrashIcon} alt="delete" />
                </button>
            </div>
            <div className="room-card-row">
                <span className="mode-info" style={{ color: typeColor }}>
                    {type}
                </span>
                <button className="settings-button" onClick={openSettingsPage}>
                    Setting
                </button>
            </div>
        </div>
    );
};

export default RoomCard;