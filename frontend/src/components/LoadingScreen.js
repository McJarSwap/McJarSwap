import React, { useState, useEffect } from "react";
import "./LoadingScreenStyle.css";

const LoadingScreen = ({ duration = 5 }) => {
  const [countdown, setCountdown] = useState(duration);

  useEffect(() => {
    // 카운트다운이 1보다 클 때만 타이머 실행
    if (countdown > 1) {
      const timer = setInterval(() => {
        setCountdown((prev) => prev - 1);
      }, 1000);

      // 컴포넌트 언마운트 시 타이머 정리
      return () => clearInterval(timer);
    }
  }, [countdown]);

  return (
    <div className="loading-screen">
      <div className="loading-text">
        In progress... Please wait {countdown} seconds
      </div>
    </div>
  );
};

export default LoadingScreen;
