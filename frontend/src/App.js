import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Main from "./Main";
import SettingsScreen from "./components/SettingsScreen";
import "./App.css";

const App = () => {
    const [rooms, setRooms] = useState([
        { name: "Room1", port: "12345", type: "Creative" },
        { name: "Room2", port: "23456", type: "Survival" },
        { name: "Room3", port: "34567", type: "Adventure" },
    ]);
    const [isLoading, setIsLoading] = useState(false);

    const addRoom = () => {
        setIsLoading(true); // 대기 화면 표시
        setTimeout(() => {
            const types = ["Survival", "Creative", "Adventure"];
            const randomType = types[Math.floor(Math.random() * types.length)];
            setRooms((prevRooms) => [
                ...prevRooms,
                { name: `NewRoom${prevRooms.length + 1}`, port: "00000", type: randomType },
            ]);
            setIsLoading(false); // 대기 화면 숨김
        }, 5000); // 5초 후 실행
    };

    const deleteRoom = (index) => {
        setRooms(rooms.filter((_, i) => i !== index));
    };

    const updateRoom = (index, updatedRoom) => {
        const newRooms = [...rooms];
        newRooms[index] = updatedRoom;
        setRooms(newRooms);
    };

    return (
        <Router>
            <Routes>
                <Route path="/" element={<Main rooms={rooms} isLoading={isLoading} addRoom={addRoom} deleteRoom={deleteRoom}/>}/>
                <Route path="/settings" element={<SettingsScreen rooms={rooms} onUpdateRoom={updateRoom}/>}/>
            </Routes>
        </Router>
    );
};

export default App;
