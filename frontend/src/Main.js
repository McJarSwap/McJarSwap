import React, { useState } from 'react';
import RoomList from './components/RoomList';
import LoadingScreen from './components/LoadingScreen';
import AddRoom from './components/AddRoom';

const Main = ({ rooms, isLoading, addRoom, deleteRoom }) => {
    const [isAddRoomOpen, setIsAddRoomOpen] = useState(false);

    const toggleAddRoomPopup = () => {
        setIsAddRoomOpen(!isAddRoomOpen);
    };

    return (
        <div className="app-container">
            {isLoading && <LoadingScreen duration={5} />}
            <header className="app-header">
                <h1>MCJarSwap</h1>
                {/*<input*/}
                {/*    className="input-box"*/}
                {/*    type="text"*/}
                {/*    placeholder="SSH IP Address"*/}
                {/*/>*/}
                {/*<button className="set-button">Set</button>*/}
                <button className="add-button" onClick={toggleAddRoomPopup}>
                    Create Room +
                </button>
            </header>
            <RoomList rooms={rooms} onDeleteRoom={deleteRoom} />
            {isAddRoomOpen && (
                <AddRoom
                    onClose={toggleAddRoomPopup}
                    onCreateRoom={(roomDetails) => {
                        addRoom(roomDetails);
                        toggleAddRoomPopup();
                    }}
                />
            )}
        </div>
    );
};

export default Main;
