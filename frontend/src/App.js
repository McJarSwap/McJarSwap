import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import axiosInstance from "./api/axios";
import Main from "./Main";
import SettingsScreen from "./components/SettingsScreen";
import "./App.css";

const App = () => {
    const [rooms, setRooms] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const fetchRooms = async () => {
        setIsLoading(true);
        try {
            const response = await axiosInstance.get("/");
            const fetchedRooms = response.data.map((room) => ({
                name: room.name,
                port: room.port,
                type: room.mode,
            }));
            setRooms(fetchedRooms);
        } catch (error) {
            console.error("Error fetching rooms:", error);
        } finally {
            setIsLoading(false);
        }
    };

    // 컴포넌트 마운트 시 데이터 가져오기
    useEffect(() => {
        fetchRooms();
    }, []);

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
