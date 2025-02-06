import React from "react";
import { useNavigate } from "react-router-dom";
import "./RoomCardStyles.css";
import TrashIcon from "../icons/TrashIcon.svg";
import axiosInstance from "../api/axios";

const RoomCard = ({ roomName, port, type }) => {
    const navigate = useNavigate();

    const openSettingsPage = () => {
        navigate(`/settings?port=${port}`, {
            state: { roomName, port, type },
        });
    };

    const handleDelete = async () => {
        const confirmDelete = window.confirm(
            `Are you sure you want to delete the room on port ${port}?`
        );
        if (!confirmDelete) return;

        try {
            const response = await axiosInstance.delete(`/delete`, {
                params: {
                    port, // 쿼리 파라미터로 전달
                },
            });

            // 성공 응답 처리
            alert(`Room on port ${port} deleted successfully.`);
            window.location.href = "/";
        } catch (error) {
            // 오류 처리
            if (error.response && error.response.data) {
                alert(`Failed to delete room: ${error.response.data.message}`);
            } else {
                alert("An error occurred while deleting the room.");
            }
            console.error("Error deleting room:", error);
        }
    };

    const typeColor =
        type === "survival" ? "red" :
            type === "creative" ? "blue" :
                type === "adventure" ? "green" : "default";

    return (
        <div className="room-card">
            <div className="room-card-row">
                <span className="room-info">{roomName}</span>
                <span className="port-info">Port: {port}</span>
                <button className="delete-button" onClick={handleDelete}>
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
