import React from "react";
import RoomCard from "./RoomCard";
import "./RoomListStyles.css";

const RoomList = ({ rooms, onDeleteRoom }) => {
    return (
        <div className="room-list">
            {rooms.map((room, index) => (
                <RoomCard
                    key={index}
                    roomName={room.name}
                    port={room.port}
                    type={room.type}
                    onDelete={() => onDeleteRoom(index)}
                />
            ))}
        </div>
    );
};

export default RoomList;