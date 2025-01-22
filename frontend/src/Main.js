import React from 'react';
import RoomList from './components/RoomList';
import LoadingScreen from './components/LoadingScreen';

const Main = ({ rooms, isLoading, addRoom, deleteRoom }) => {
    return (
        <div className="app-container">
            {isLoading && <LoadingScreen duration={5}/>}
            <header className="app-header">
                <h1>MCJarSwap</h1>
                <input
                    className="input-box"
                    type="text"
                    placeholder="SSH IP Address"
                />
                <button className="set-button">Set</button>
                <button className="add-button" onClick={addRoom}>
                    Add Room +
                </button>
            </header>
            <RoomList rooms={rooms} onDeleteRoom={deleteRoom} />
        </div>
    );
};

export default Main;
