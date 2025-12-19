-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 19, 2025 at 09:23 PM
-- Server version: 8.3.0
-- PHP Version: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `transportportal`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `action` varchar(100) NOT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `record_id` int DEFAULT NULL,
  `old_values` json DEFAULT NULL,
  `new_values` json DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `audit_logs`
--

INSERT INTO `audit_logs` (`id`, `user_id`, `action`, `table_name`, `record_id`, `old_values`, `new_values`, `ip_address`, `user_agent`, `created_at`) VALUES
(1, 1, 'CREATE', 'drivers', 1, NULL, NULL, '192.168.1.100', NULL, '2025-11-03 15:15:09'),
(2, 1, 'UPDATE', 'vehicles', 1, NULL, NULL, '192.168.1.100', NULL, '2025-11-03 15:15:09'),
(3, 2, 'LOGIN', 'users', 2, NULL, NULL, '192.168.1.101', NULL, '2025-11-03 15:15:09');

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trip_id` int NOT NULL,
  `user_id` int NOT NULL,
  `seats` int NOT NULL DEFAULT '1',
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
  `booked_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `trip_id` (`trip_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`id`, `trip_id`, `user_id`, `seats`, `total_price`, `status`, `booked_at`, `created_at`, `updated_at`) VALUES
(1, 1, 5, 2, 10000.00, 'CONFIRMED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(2, 1, 6, 1, 5000.00, 'CONFIRMED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(3, 2, 7, 3, 18000.00, 'CONFIRMED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(4, 3, 5, 2, 14000.00, 'PENDING', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08');

-- --------------------------------------------------------

--
-- Table structure for table `drivers`
--

DROP TABLE IF EXISTS `drivers`;
CREATE TABLE IF NOT EXISTS `drivers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `license_number` varchar(50) NOT NULL,
  `experience_years` int DEFAULT '0',
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `license_number` (`license_number`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `drivers`
--

INSERT INTO `drivers` (`id`, `user_id`, `license_number`, `experience_years`, `status`, `created_at`) VALUES
(1, 2, 'DL001', 5, 'ACTIVE', '2025-11-03 10:00:48'),
(2, 3, 'DL002', 3, 'ACTIVE', '2025-11-03 10:00:48'),
(12, 18, '12', 0, 'ACTIVE', '2025-11-03 18:37:52'),
(4, 6, '77', 6, 'ACTIVE', '2025-11-03 14:27:47'),
(5, 7, '34A', 7, 'ACTIVE', '2025-11-03 15:29:23'),
(6, 9, '33A', 7, 'ACTIVE', '2025-11-03 15:35:10'),
(16, 29, '099', 2, 'ACTIVE', '2025-12-10 17:58:03'),
(9, 15, 'DL021', 5, 'ACTIVE', '2025-11-03 17:42:18'),
(14, 25, '5510', 30, 'ACTIVE', '2025-11-06 17:44:33'),
(11, 16, '098', 10, 'ACTIVE', '2025-11-03 18:06:01'),
(15, 28, '1357', 0, 'ACTIVE', '2025-11-06 18:15:39');

-- --------------------------------------------------------

--
-- Table structure for table `driver_vehicle_assignments`
--

DROP TABLE IF EXISTS `driver_vehicle_assignments`;
CREATE TABLE IF NOT EXISTS `driver_vehicle_assignments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `driver_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `assigned_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_active_assignment` (`driver_id`,`vehicle_id`,`status`),
  KEY `vehicle_id` (`vehicle_id`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `driver_vehicle_assignments`
--

INSERT INTO `driver_vehicle_assignments` (`id`, `driver_id`, `vehicle_id`, `assigned_date`, `status`) VALUES
(1, 1, 1, '2025-11-03 14:40:46', 'ACTIVE'),
(2, 2, 2, '2025-11-03 14:40:46', 'ACTIVE'),
(3, 4, 4, '2025-11-03 15:25:00', 'ACTIVE'),
(4, 5, 5, '2025-11-03 15:30:46', 'INACTIVE'),
(5, 6, 5, '2025-11-03 15:35:33', 'INACTIVE'),
(6, 7, 6, '2025-11-03 16:55:55', 'INACTIVE'),
(7, 8, 7, '2025-11-03 17:04:06', 'INACTIVE'),
(8, 9, 8, '2025-11-03 17:43:49', 'INACTIVE'),
(9, 9, 8, '2025-11-03 17:47:43', 'ACTIVE'),
(10, 10, 7, '2025-11-03 17:49:40', 'INACTIVE'),
(11, 7, 7, '2025-11-03 17:57:36', 'ACTIVE'),
(12, 11, 9, '2025-11-03 18:08:15', 'INACTIVE'),
(13, 11, 9, '2025-11-03 18:08:52', 'ACTIVE'),
(14, 12, 3, '2025-11-03 18:38:26', 'ACTIVE'),
(15, 13, 10, '2025-11-05 15:09:05', 'INACTIVE'),
(16, 13, 10, '2025-11-05 15:09:22', 'ACTIVE'),
(17, 6, 5, '2025-12-17 15:51:43', 'ACTIVE');

-- --------------------------------------------------------

--
-- Table structure for table `maintenance`
--

DROP TABLE IF EXISTS `maintenance`;
CREATE TABLE IF NOT EXISTS `maintenance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `description` text NOT NULL,
  `cost` decimal(10,2) DEFAULT '0.00',
  `maintenance_date` date NOT NULL,
  `next_maintenance_date` date DEFAULT NULL,
  `status` enum('SCHEDULED','IN_PROGRESS','COMPLETED') DEFAULT 'SCHEDULED',
  `remarks` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `vehicle_id` (`vehicle_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `maintenance`
--

INSERT INTO `maintenance` (`id`, `vehicle_id`, `description`, `cost`, `maintenance_date`, `next_maintenance_date`, `status`, `remarks`, `created_at`, `updated_at`) VALUES
(1, 1, 'Regular service and oil change', 85000.00, '2025-11-03', NULL, 'COMPLETED', NULL, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(2, 2, 'Brake system inspection', 45000.00, '2025-11-10', NULL, 'SCHEDULED', NULL, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(3, 3, 'Tire replacement', 120000.00, '2025-10-19', NULL, 'COMPLETED', NULL, '2025-11-03 15:15:08', '2025-11-03 15:15:08');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` enum('CASH','CARD','MOBILE_MONEY') DEFAULT 'CASH',
  `status` enum('PENDING','COMPLETED','FAILED','REFUNDED') DEFAULT 'PENDING',
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `booking_id` (`booking_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`id`, `booking_id`, `amount`, `method`, `status`, `paid_at`, `created_at`, `updated_at`) VALUES
(1, 1, 10000.00, 'MOBILE_MONEY', 'COMPLETED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(2, 2, 5000.00, 'CASH', 'COMPLETED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(3, 3, 18000.00, 'CARD', 'COMPLETED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', '2025-11-03 15:15:08');

-- --------------------------------------------------------

--
-- Table structure for table `routes`
--

DROP TABLE IF EXISTS `routes`;
CREATE TABLE IF NOT EXISTS `routes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `origin` varchar(100) NOT NULL,
  `destination` varchar(100) NOT NULL,
  `distance_km` decimal(8,2) NOT NULL,
  `base_fare` decimal(10,2) DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `routes`
--

INSERT INTO `routes` (`id`, `origin`, `destination`, `distance_km`, `base_fare`, `created_at`, `updated_at`) VALUES
(1, 'Kigali', 'Musanze', 105.50, 2500.00, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(2, 'Kigali', 'Huye', 135.20, 3000.00, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(3, 'Kigali', 'Rubavu', 155.80, 3500.00, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(4, 'Kigali', 'Nyagatare', 180.30, 4000.00, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(5, 'Kigali', 'Rusizi', 220.70, 4500.00, '2025-11-03 15:15:08', '2025-11-03 15:15:08'),
(6, 'Rubengera', 'Rugerero', 5.00, 0.00, '2025-11-05 16:50:03', '2025-11-05 16:50:03');

-- --------------------------------------------------------

--
-- Table structure for table `trips`
--

DROP TABLE IF EXISTS `trips`;
CREATE TABLE IF NOT EXISTS `trips` (
  `id` int NOT NULL AUTO_INCREMENT,
  `route_id` int NOT NULL,
  `driver_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` enum('SCHEDULED','STARTED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `available_seats` int DEFAULT '15',
  PRIMARY KEY (`id`),
  KEY `route_id` (`route_id`),
  KEY `driver_id` (`driver_id`),
  KEY `vehicle_id` (`vehicle_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `trips`
--

INSERT INTO `trips` (`id`, `route_id`, `driver_id`, `vehicle_id`, `departure_time`, `arrival_time`, `price`, `status`, `created_at`, `updated_at`, `available_seats`) VALUES
(1, 1, 1, 1, '2025-11-03 19:15:08', NULL, 5000.00, 'SCHEDULED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', 15),
(2, 2, 2, 2, '2025-11-03 21:15:08', NULL, 6000.00, 'SCHEDULED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', 15),
(3, 3, 3, 3, '2025-11-03 23:15:08', NULL, 7000.00, 'SCHEDULED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', 15),
(4, 1, 1, 1, '2025-11-04 17:15:08', NULL, 5000.00, 'SCHEDULED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', 15),
(5, 2, 2, 2, '2025-11-04 17:15:08', NULL, 6000.00, 'SCHEDULED', '2025-11-03 15:15:08', '2025-11-03 15:15:08', 15),
(6, 6, 10, 10, '2025-11-07 17:26:00', NULL, 5000.00, 'SCHEDULED', '2025-11-05 17:27:20', '2025-11-05 17:27:20', 15),
(7, 5, 13, 10, '2025-11-08 17:27:00', NULL, 3300.00, 'SCHEDULED', '2025-11-05 17:28:50', '2025-11-05 17:28:50', 15);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` enum('ADMIN','DRIVER','PASSENGER') DEFAULT 'PASSENGER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `contact`, `email`, `role`, `created_at`) VALUES
(1, 'admin', 'admin123', 'System Administrator', '0780000000', NULL, 'ADMIN', '2025-11-03 10:00:48'),
(2, 'johndriver', 'driver123', 'John Driver', '0781234567', NULL, 'DRIVER', '2025-11-03 10:00:48'),
(3, 'janesmith', 'driver123', 'Jane Smith', '0787654321', NULL, 'DRIVER', '2025-11-03 10:00:48'),
(4, 'mikejohnson', 'driver123', 'Mike Johnson', '0785556666', NULL, 'DRIVER', '2025-11-03 10:00:48'),
(5, 'aphrodis', 'dfgfg', 'fdbgnhn n', NULL, NULL, 'DRIVER', '2025-11-03 10:25:58'),
(6, 'eric349', 'driver123', 'eric', '7847478', NULL, 'DRIVER', '2025-11-03 14:27:47'),
(9, 'ishimwevalens56', 'driver123', 'Ishimwe Valens', '56566776', NULL, 'DRIVER', '2025-11-03 15:35:10'),
(8, 'Valens', '123456', 'Ishimwe Valens', NULL, NULL, 'DRIVER', '2025-11-03 15:32:23'),
(10, 'imanishimwe2', 'driver123', 'Imanishimwe', '55667', NULL, 'DRIVER', '2025-11-03 16:55:08'),
(11, 'Piere', 'habipiere', 'Habimana Piere', NULL, NULL, 'DRIVER', '2025-11-03 16:59:56'),
(12, 'Aphrodis1', 'umugore', 'Niyonsenga Aphrodis', NULL, NULL, 'DRIVER', '2025-11-03 17:10:22'),
(13, 'Aphrod', 'umugore', 'Niyonsenga Aphrodis', NULL, NULL, 'DRIVER', '2025-11-03 17:10:55'),
(14, 'Niyikiza', 'niyikiza', 'Niyikiza', NULL, NULL, 'DRIVER', '2025-11-03 17:12:29'),
(15, 'lea591', 'driver123', 'Lea', '0780615240', NULL, 'DRIVER', '2025-11-03 17:42:18'),
(16, 'olivier868', 'driver123', 'olivier', '078234567', NULL, 'DRIVER', '2025-11-03 18:06:01'),
(17, 'olivier', 'olivier123', 'olivier ishimwe', '056789', 'olivier@gmail.com', 'PASSENGER', '2025-11-03 18:12:47'),
(29, 'passenger', '1234', 'Bizimana Emmanuel', '0793000400', 'biz@gmail.com', 'DRIVER', '2025-12-10 17:58:03'),
(28, 'Nicore', 'nicore', 'bnds', '564356436', 'bndshndsh', 'DRIVER', '2025-11-06 18:15:39'),
(27, 'HHHH', 'HHHH', 'HHHH', '34456567', 'GHHJHJ', 'DRIVER', '2025-11-06 17:56:09'),
(26, 'moise', 'mo123', 'iranzi moise', '07983456', 'iranzi@gmail.com', 'DRIVER', '2025-11-06 17:52:45'),
(24, 'divine', 'di123', 'sezerano divine', '0789999000', 'divine@gmail.com', 'DRIVER', '2025-11-06 17:26:19'),
(25, 'sandrine', 'san123', 'agwize sandrine', '0782222333', 'agwize@gmail.com', 'DRIVER', '2025-11-06 17:44:33');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `plate_number` varchar(20) NOT NULL,
  `model` varchar(100) NOT NULL,
  `capacity` int NOT NULL,
  `status` enum('AVAILABLE','MAINTENANCE','IN_USE') DEFAULT 'AVAILABLE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `plate_number` (`plate_number`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`id`, `plate_number`, `model`, `capacity`, `status`, `created_at`) VALUES
(1, 'RAA 123A', 'Volcano Bus', 45, 'AVAILABLE', '2025-11-03 10:00:52'),
(2, 'RAB 456B', 'RITCO Coach', 45, 'AVAILABLE', '2025-11-03 10:00:52'),
(3, 'RAC 789B', 'Express Van', 45, 'AVAILABLE', '2025-11-03 10:00:52'),
(4, 'we334545', 'dgf55', 3, 'AVAILABLE', '2025-11-03 10:27:10'),
(5, 'RAC502L', 'Move', 50, 'AVAILABLE', '2025-11-03 15:30:23'),
(10, 'RAB22', 'Prado', 6, 'AVAILABLE', '2025-11-05 15:07:35'),
(7, 'RAC 317L', 'DL030', 64, 'AVAILABLE', '2025-11-03 17:03:55'),
(8, 'RG317Z', 'Stella express', 50, 'AVAILABLE', '2025-11-03 17:43:38'),
(9, 'RAI510Q', 'novel model', 45, 'AVAILABLE', '2025-11-03 18:07:26');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
