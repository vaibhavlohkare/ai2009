-- phpMyAdmin SQL Dump
-- version 3.2.0.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 29, 2009 at 05:09 PM
-- Server version: 5.1.37
-- PHP Version: 5.3.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `AIFriends`
--

-- --------------------------------------------------------

--
-- Table structure for table `Links`
--

CREATE TABLE IF NOT EXISTS `Links` (
  `linkID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `link` varchar(255) NOT NULL,
  PRIMARY KEY (`linkID`),
  UNIQUE KEY `link` (`link`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

--
-- Dumping data for table `Links`
--

INSERT INTO `Links` (`linkID`, `link`) VALUES
(1, 'http://www.yelp.com/user_details?userid=ei8X5pyCur3d0CGb5EbnFA'),
(2, 'http://www.yelp.com/user_details?userid=IdV7Y5syspxx3GLK6L1KHg'),
(3, 'http://www.yelp.com/user_details?userid=tQzP0AiUXoQve5uFbjZUEQ'),
(4, 'http://www.yelp.com/user_details?userid=pW9LvtC-hZ0PRAOUn7aZ2A'),
(5, 'http://www.yelp.com/user_details?userid=RgDVC3ZUBqpEe6Y1kPhIpw'),
(6, 'http://www.yelp.com/user_details?userid=c4eh4aq5Bw9zVMt4xLG2ew'),
(7, 'http://www.yelp.com/user_details?userid=BmThnFPDalpbC1x98aXKaw'),
(8, 'http://www.yelp.com/user_details?userid=ckpY2NZ2LDD5PBryBNzkmw'),
(9, 'http://www.yelp.com/user_details?userid=6s-g2vFu12OemhiK3FJuOQ'),
(10, 'http://www.yelp.com/user_details?userid=_BcWyKQL16ndpBdggh2kNA'),
(11, 'http://www.yelp.com/user_details?userid=dLOfp-2TAGAb78jGZPvMoA');
