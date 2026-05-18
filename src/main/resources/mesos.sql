CREATE DATABASE IF NOT EXISTS `mesos`;
USE `mesos`;

CREATE TABLE IF NOT EXISTS `mesos_leaderboard` (
`id` int NOT NULL AUTO_INCREMENT,
`nickname` varchar(255) NOT NULL,
`final_score` int NOT NULL,
`game_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`players_count` int NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
