-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: jadwale_db
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `jadwale_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `jadwale_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `jadwale_db`;

--
-- Table structure for table `adminlog`
--

DROP TABLE IF EXISTS `adminlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adminlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_user_id` int(11) NOT NULL,
  `action` varchar(191) NOT NULL,
  `target_type` varchar(191) DEFAULT NULL,
  `target_id` int(11) DEFAULT NULL,
  `details` varchar(191) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (`id`),
  KEY `AdminLog_admin_user_id_idx` (`admin_user_id`),
  CONSTRAINT `AdminLog_admin_user_id_fkey` FOREIGN KEY (`admin_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adminlog`
--

LOCK TABLES `adminlog` WRITE;
/*!40000 ALTER TABLE `adminlog` DISABLE KEYS */;
INSERT INTO `adminlog` VALUES (8,66,'VERIFIKASI_SEKOLAH_AKTIF','Sekolah',11,'{\"nama\":\"SDN Pancasila 01\",\"npsn\":\"20108922\",\"status\":\"ACTIVE\"}','2026-10-03 02:31:26.814'),(9,66,'SINKRONISASI_DAPODIK_WILAYAH','System',1,'{\"totalSekolah\":18,\"status\":\"SUCCESS\",\"verified\":true}','2026-10-04 02:31:26.814'),(10,66,'GENERATOR_CSP_SELESAI','Jadwal',1,'{\"slots\":348,\"bebasBentrok\":true,\"executionTimeSec\":3.2}','2026-10-05 00:31:26.815'),(11,66,'EKSPOR_CADANGAN_DATABASE','Database',1,'{\"format\":\"JSON/SQL\",\"fileSize\":\"78KB\",\"checksum\":\"VALID\"}','2026-10-05 01:31:26.815');
/*!40000 ALTER TABLE `adminlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guru`
--

DROP TABLE IF EXISTS `guru`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guru` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `nama` varchar(191) NOT NULL,
  `nip` varchar(191) DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Guru_nip_key` (`nip`),
  KEY `Guru_id_sekolah_idx` (`id_sekolah`),
  CONSTRAINT `Guru_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guru`
--

LOCK TABLES `guru` WRITE;
/*!40000 ALTER TABLE `guru` DISABLE KEYS */;
INSERT INTO `guru` VALUES (44,11,'Bambang Sutrisno, M.Pd.','198204152008011008',NULL,'2026-10-05 02:31:24.026','2026-10-05 02:31:24.026'),(45,11,'Siti Aminah, S.Pd.','198811202012022004',NULL,'2026-10-05 02:31:24.071','2026-10-05 02:31:24.071'),(46,11,'Ahmad Fauzi, S.Pd.','197912202006011003',NULL,'2026-10-05 02:31:24.121','2026-10-05 02:31:24.121'),(47,11,'Dewi Lestari, S.Pd.','199001102015012004',NULL,'2026-10-05 02:31:24.167','2026-10-05 02:31:24.167'),(48,11,'Hendra Gunawan, S.Pd.','198708052012011005',NULL,'2026-10-05 02:31:24.200','2026-10-05 02:31:24.200'),(49,11,'Rina Oktavia, S.Pd.I','199205182017012006',NULL,'2026-10-05 02:31:24.336','2026-10-05 02:31:24.336'),(50,11,'Wahyu Setiawan, S.Pd.','198411272009011007',NULL,'2026-10-05 02:31:24.491','2026-10-05 02:31:24.491'),(51,11,'Nurul Hidayah, S.Pd.','199107082016012008',NULL,'2026-10-05 02:31:24.511','2026-10-05 02:31:24.511'),(52,11,'Agus Prasetyo, S.Pd.','198006142004011009',NULL,'2026-10-05 02:31:24.534','2026-10-05 02:31:24.534'),(53,11,'Maya Sari, S.Pd.','199310222019012010',NULL,'2026-10-05 02:31:24.546','2026-10-05 02:31:24.546'),(54,11,'Doni Firmansyah, S.Pd.','198802132011011011',NULL,'2026-10-05 02:31:24.570','2026-10-05 02:31:24.570'),(55,11,'Sri Wulandari, S.Pd.','197805302003012012',NULL,'2026-10-05 02:31:24.583','2026-10-05 02:31:24.583'),(56,11,'Ratna Kartika, S.Pd.','199504122020122015',NULL,'2026-10-05 02:31:24.599','2026-10-05 02:31:24.599'),(57,11,'Joko Widodo, S.Pd.','198103142005011002',NULL,'2026-10-05 02:31:24.612','2026-10-05 02:31:24.612');
/*!40000 ALTER TABLE `guru` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guruavailability`
--

DROP TABLE IF EXISTS `guruavailability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guruavailability` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_guru` int(11) NOT NULL,
  `hari` int(11) NOT NULL,
  `jam_mulai` datetime(3) DEFAULT NULL,
  `jam_selesai` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `GuruAvailability_id_sekolah_id_guru_hari_key` (`id_sekolah`,`id_guru`,`hari`),
  KEY `GuruAvailability_id_guru_fkey` (`id_guru`),
  CONSTRAINT `GuruAvailability_id_guru_fkey` FOREIGN KEY (`id_guru`) REFERENCES `guru` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `GuruAvailability_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guruavailability`
--

LOCK TABLES `guruavailability` WRITE;
/*!40000 ALTER TABLE `guruavailability` DISABLE KEYS */;
INSERT INTO `guruavailability` VALUES (10,11,48,2,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(11,11,48,3,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(12,11,48,4,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(13,11,48,5,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(14,11,49,1,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(15,11,49,2,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(16,11,49,3,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(17,11,49,4,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000'),(18,11,49,5,'1970-01-01 07:00:00.000','1970-01-01 12:00:00.000');
/*!40000 ALTER TABLE `guruavailability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `aksi` varchar(191) NOT NULL,
  `deskripsi` varchar(191) DEFAULT NULL,
  `ip_address` varchar(191) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (`id`),
  KEY `History_id_sekolah_user_id_idx` (`id_sekolah`,`user_id`),
  KEY `History_user_id_fkey` (`user_id`),
  CONSTRAINT `History_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `History_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES (3,11,68,'UPDATE_CONFIG_SEKOLAH','Mengatur struktur kelas paralel dan alokasi 35 menit per JP','127.0.0.1','2026-10-05 02:31:26.832'),(4,11,68,'GENERATE_JADWAL','Generate jadwal pelajaran semester ganjil (12 rombel)','127.0.0.1','2026-10-05 02:31:26.832');
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jadwal`
--

DROP TABLE IF EXISTS `jadwal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jadwal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_periode_jadwal` int(11) DEFAULT NULL,
  `id_kelas` int(11) NOT NULL,
  `hari` int(11) NOT NULL,
  `jam_ke` int(11) NOT NULL,
  `id_mapel` int(11) NOT NULL,
  `id_guru` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Jadwal_id_sekolah_id_kelas_idx` (`id_sekolah`,`id_kelas`),
  KEY `Jadwal_id_guru_hari_jam_ke_idx` (`id_guru`,`hari`,`jam_ke`),
  KEY `Jadwal_id_periode_jadwal_idx` (`id_periode_jadwal`),
  KEY `Jadwal_id_kelas_fkey` (`id_kelas`),
  KEY `Jadwal_id_mapel_fkey` (`id_mapel`),
  CONSTRAINT `Jadwal_id_guru_fkey` FOREIGN KEY (`id_guru`) REFERENCES `guru` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `Jadwal_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Jadwal_id_mapel_fkey` FOREIGN KEY (`id_mapel`) REFERENCES `mapel` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `Jadwal_id_periode_jadwal_fkey` FOREIGN KEY (`id_periode_jadwal`) REFERENCES `periodejadwal` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Jadwal_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1735 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jadwal`
--

LOCK TABLES `jadwal` WRITE;
/*!40000 ALTER TABLE `jadwal` DISABLE KEYS */;
INSERT INTO `jadwal` VALUES (1387,11,1,37,1,1,39,44),(1388,11,1,37,1,2,39,44),(1389,11,1,37,1,3,40,44),(1390,11,1,37,1,4,40,44),(1391,11,1,37,1,5,40,44),(1392,11,1,37,1,6,40,44),(1393,11,1,37,2,1,41,44),(1394,11,1,37,2,2,41,44),(1395,11,1,37,2,3,42,44),(1396,11,1,37,2,4,42,44),(1397,11,1,37,2,5,40,44),(1398,11,1,37,2,6,40,44),(1399,11,1,37,3,1,43,48),(1400,11,1,37,3,2,43,48),(1401,11,1,37,3,3,40,44),(1402,11,1,37,3,4,40,44),(1403,11,1,37,3,5,40,44),(1404,11,1,37,3,6,40,44),(1405,11,1,37,4,1,38,49),(1406,11,1,37,4,2,38,49),(1407,11,1,37,4,3,45,50),(1408,11,1,37,4,4,45,50),(1409,11,1,37,4,5,40,44),(1410,11,1,37,4,6,40,44),(1411,11,1,37,5,1,46,44),(1412,11,1,37,5,2,44,44),(1413,11,1,37,5,3,46,44),(1414,11,1,37,5,4,44,44),(1415,11,1,37,5,5,46,44),(1416,11,1,38,1,1,39,45),(1417,11,1,38,1,2,39,45),(1418,11,1,38,1,3,40,45),(1419,11,1,38,1,4,40,45),(1420,11,1,38,1,5,40,45),(1421,11,1,38,1,6,40,45),(1422,11,1,38,2,1,41,45),(1423,11,1,38,2,2,41,45),(1424,11,1,38,2,3,42,45),(1425,11,1,38,2,4,42,45),(1426,11,1,38,2,5,40,45),(1427,11,1,38,2,6,40,45),(1428,11,1,38,3,1,41,45),(1429,11,1,38,3,2,41,45),(1430,11,1,38,3,3,40,45),(1431,11,1,38,3,4,40,45),(1432,11,1,38,3,5,40,45),(1433,11,1,38,3,6,40,45),(1434,11,1,38,4,1,38,49),(1435,11,1,38,4,2,38,49),(1436,11,1,38,4,3,45,50),(1437,11,1,38,4,4,45,50),(1438,11,1,38,4,5,40,45),(1439,11,1,38,4,6,40,45),(1440,11,1,38,5,1,46,45),(1441,11,1,38,5,2,44,45),(1442,11,1,38,5,3,46,45),(1443,11,1,38,5,4,44,45),(1444,11,1,38,5,5,46,45),(1445,11,1,39,1,1,39,46),(1446,11,1,39,1,2,39,46),(1447,11,1,39,1,3,40,46),(1448,11,1,39,1,4,40,46),(1449,11,1,39,1,5,40,46),(1450,11,1,39,1,6,40,46),(1451,11,1,39,2,1,41,46),(1452,11,1,39,2,2,41,46),(1453,11,1,39,2,3,42,46),(1454,11,1,39,2,4,42,46),(1455,11,1,39,2,5,40,46),(1456,11,1,39,2,6,40,46),(1457,11,1,39,3,1,43,48),(1458,11,1,39,3,2,43,48),(1459,11,1,39,3,3,40,46),(1460,11,1,39,3,4,40,46),(1461,11,1,39,3,5,40,46),(1462,11,1,39,3,6,40,46),(1463,11,1,39,4,1,38,49),(1464,11,1,39,4,2,38,49),(1465,11,1,39,4,3,45,50),(1466,11,1,39,4,4,45,50),(1467,11,1,39,4,5,40,46),(1468,11,1,39,4,6,40,46),(1469,11,1,39,5,1,46,46),(1470,11,1,39,5,2,44,46),(1471,11,1,39,5,3,46,46),(1472,11,1,39,5,4,44,46),(1473,11,1,39,5,5,46,46),(1474,11,1,40,1,1,39,47),(1475,11,1,40,1,2,39,47),(1476,11,1,40,1,3,40,47),(1477,11,1,40,1,4,40,47),(1478,11,1,40,1,5,40,47),(1479,11,1,40,1,6,40,47),(1480,11,1,40,2,1,41,47),(1481,11,1,40,2,2,41,47),(1482,11,1,40,2,3,42,47),(1483,11,1,40,2,4,42,47),(1484,11,1,40,2,5,40,47),(1485,11,1,40,2,6,40,47),(1486,11,1,40,3,1,41,47),(1487,11,1,40,3,2,41,47),(1488,11,1,40,3,3,40,47),(1489,11,1,40,3,4,40,47),(1490,11,1,40,3,5,40,47),(1491,11,1,40,3,6,40,47),(1492,11,1,40,4,1,38,49),(1493,11,1,40,4,2,38,49),(1494,11,1,40,4,3,45,50),(1495,11,1,40,4,4,45,50),(1496,11,1,40,4,5,40,47),(1497,11,1,40,4,6,40,47),(1498,11,1,40,5,1,46,47),(1499,11,1,40,5,2,44,47),(1500,11,1,40,5,3,46,47),(1501,11,1,40,5,4,44,47),(1502,11,1,40,5,5,46,47),(1503,11,1,41,1,1,39,48),(1504,11,1,41,1,2,39,48),(1505,11,1,41,1,3,40,48),(1506,11,1,41,1,4,40,48),(1507,11,1,41,1,5,40,48),(1508,11,1,41,1,6,40,48),(1509,11,1,41,2,1,41,48),(1510,11,1,41,2,2,41,48),(1511,11,1,41,2,3,42,48),(1512,11,1,41,2,4,42,48),(1513,11,1,41,2,5,40,48),(1514,11,1,41,2,6,40,48),(1515,11,1,41,3,1,43,48),(1516,11,1,41,3,2,43,48),(1517,11,1,41,3,3,40,48),(1518,11,1,41,3,4,40,48),(1519,11,1,41,3,5,40,48),(1520,11,1,41,3,6,40,48),(1521,11,1,41,4,1,38,49),(1522,11,1,41,4,2,38,49),(1523,11,1,41,4,3,45,50),(1524,11,1,41,4,4,45,50),(1525,11,1,41,4,5,40,48),(1526,11,1,41,4,6,40,48),(1527,11,1,41,5,1,46,48),(1528,11,1,41,5,2,44,48),(1529,11,1,41,5,3,46,48),(1530,11,1,41,5,4,44,48),(1531,11,1,41,5,5,46,48),(1532,11,1,42,1,1,39,49),(1533,11,1,42,1,2,39,49),(1534,11,1,42,1,3,40,49),(1535,11,1,42,1,4,40,49),(1536,11,1,42,1,5,40,49),(1537,11,1,42,1,6,40,49),(1538,11,1,42,2,1,41,49),(1539,11,1,42,2,2,41,49),(1540,11,1,42,2,3,42,49),(1541,11,1,42,2,4,42,49),(1542,11,1,42,2,5,40,49),(1543,11,1,42,2,6,40,49),(1544,11,1,42,3,1,41,49),(1545,11,1,42,3,2,41,49),(1546,11,1,42,3,3,40,49),(1547,11,1,42,3,4,40,49),(1548,11,1,42,3,5,40,49),(1549,11,1,42,3,6,40,49),(1550,11,1,42,4,1,38,49),(1551,11,1,42,4,2,38,49),(1552,11,1,42,4,3,45,50),(1553,11,1,42,4,4,45,50),(1554,11,1,42,4,5,40,49),(1555,11,1,42,4,6,40,49),(1556,11,1,42,5,1,46,49),(1557,11,1,42,5,2,44,49),(1558,11,1,42,5,3,46,49),(1559,11,1,42,5,4,44,49),(1560,11,1,42,5,5,46,49),(1561,11,1,43,1,1,39,50),(1562,11,1,43,1,2,39,50),(1563,11,1,43,1,3,40,50),(1564,11,1,43,1,4,40,50),(1565,11,1,43,1,5,40,50),(1566,11,1,43,1,6,40,50),(1567,11,1,43,2,1,41,50),(1568,11,1,43,2,2,41,50),(1569,11,1,43,2,3,42,50),(1570,11,1,43,2,4,42,50),(1571,11,1,43,2,5,40,50),(1572,11,1,43,2,6,40,50),(1573,11,1,43,3,1,43,48),(1574,11,1,43,3,2,43,48),(1575,11,1,43,3,3,40,50),(1576,11,1,43,3,4,40,50),(1577,11,1,43,3,5,40,50),(1578,11,1,43,3,6,40,50),(1579,11,1,43,4,1,38,49),(1580,11,1,43,4,2,38,49),(1581,11,1,43,4,3,45,50),(1582,11,1,43,4,4,45,50),(1583,11,1,43,4,5,40,50),(1584,11,1,43,4,6,40,50),(1585,11,1,43,5,1,46,50),(1586,11,1,43,5,2,44,50),(1587,11,1,43,5,3,46,50),(1588,11,1,43,5,4,44,50),(1589,11,1,43,5,5,46,50),(1590,11,1,44,1,1,39,51),(1591,11,1,44,1,2,39,51),(1592,11,1,44,1,3,40,51),(1593,11,1,44,1,4,40,51),(1594,11,1,44,1,5,40,51),(1595,11,1,44,1,6,40,51),(1596,11,1,44,2,1,41,51),(1597,11,1,44,2,2,41,51),(1598,11,1,44,2,3,42,51),(1599,11,1,44,2,4,42,51),(1600,11,1,44,2,5,40,51),(1601,11,1,44,2,6,40,51),(1602,11,1,44,3,1,41,51),(1603,11,1,44,3,2,41,51),(1604,11,1,44,3,3,40,51),(1605,11,1,44,3,4,40,51),(1606,11,1,44,3,5,40,51),(1607,11,1,44,3,6,40,51),(1608,11,1,44,4,1,38,49),(1609,11,1,44,4,2,38,49),(1610,11,1,44,4,3,45,50),(1611,11,1,44,4,4,45,50),(1612,11,1,44,4,5,40,51),(1613,11,1,44,4,6,40,51),(1614,11,1,44,5,1,46,51),(1615,11,1,44,5,2,44,51),(1616,11,1,44,5,3,46,51),(1617,11,1,44,5,4,44,51),(1618,11,1,44,5,5,46,51),(1619,11,1,45,1,1,39,52),(1620,11,1,45,1,2,39,52),(1621,11,1,45,1,3,40,52),(1622,11,1,45,1,4,40,52),(1623,11,1,45,1,5,40,52),(1624,11,1,45,1,6,40,52),(1625,11,1,45,2,1,41,52),(1626,11,1,45,2,2,41,52),(1627,11,1,45,2,3,42,52),(1628,11,1,45,2,4,42,52),(1629,11,1,45,2,5,40,52),(1630,11,1,45,2,6,40,52),(1631,11,1,45,3,1,43,48),(1632,11,1,45,3,2,43,48),(1633,11,1,45,3,3,40,52),(1634,11,1,45,3,4,40,52),(1635,11,1,45,3,5,40,52),(1636,11,1,45,3,6,40,52),(1637,11,1,45,4,1,38,49),(1638,11,1,45,4,2,38,49),(1639,11,1,45,4,3,45,50),(1640,11,1,45,4,4,45,50),(1641,11,1,45,4,5,40,52),(1642,11,1,45,4,6,40,52),(1643,11,1,45,5,1,46,52),(1644,11,1,45,5,2,44,52),(1645,11,1,45,5,3,46,52),(1646,11,1,45,5,4,44,52),(1647,11,1,45,5,5,46,52),(1648,11,1,46,1,1,39,53),(1649,11,1,46,1,2,39,53),(1650,11,1,46,1,3,40,53),(1651,11,1,46,1,4,40,53),(1652,11,1,46,1,5,40,53),(1653,11,1,46,1,6,40,53),(1654,11,1,46,2,1,41,53),(1655,11,1,46,2,2,41,53),(1656,11,1,46,2,3,42,53),(1657,11,1,46,2,4,42,53),(1658,11,1,46,2,5,40,53),(1659,11,1,46,2,6,40,53),(1660,11,1,46,3,1,41,53),(1661,11,1,46,3,2,41,53),(1662,11,1,46,3,3,40,53),(1663,11,1,46,3,4,40,53),(1664,11,1,46,3,5,40,53),(1665,11,1,46,3,6,40,53),(1666,11,1,46,4,1,38,49),(1667,11,1,46,4,2,38,49),(1668,11,1,46,4,3,45,50),(1669,11,1,46,4,4,45,50),(1670,11,1,46,4,5,40,53),(1671,11,1,46,4,6,40,53),(1672,11,1,46,5,1,46,53),(1673,11,1,46,5,2,44,53),(1674,11,1,46,5,3,46,53),(1675,11,1,46,5,4,44,53),(1676,11,1,46,5,5,46,53),(1677,11,1,47,1,1,39,54),(1678,11,1,47,1,2,39,54),(1679,11,1,47,1,3,40,54),(1680,11,1,47,1,4,40,54),(1681,11,1,47,1,5,40,54),(1682,11,1,47,1,6,40,54),(1683,11,1,47,2,1,41,54),(1684,11,1,47,2,2,41,54),(1685,11,1,47,2,3,42,54),(1686,11,1,47,2,4,42,54),(1687,11,1,47,2,5,40,54),(1688,11,1,47,2,6,40,54),(1689,11,1,47,3,1,43,48),(1690,11,1,47,3,2,43,48),(1691,11,1,47,3,3,40,54),(1692,11,1,47,3,4,40,54),(1693,11,1,47,3,5,40,54),(1694,11,1,47,3,6,40,54),(1695,11,1,47,4,1,38,49),(1696,11,1,47,4,2,38,49),(1697,11,1,47,4,3,45,50),(1698,11,1,47,4,4,45,50),(1699,11,1,47,4,5,40,54),(1700,11,1,47,4,6,40,54),(1701,11,1,47,5,1,46,54),(1702,11,1,47,5,2,44,54),(1703,11,1,47,5,3,46,54),(1704,11,1,47,5,4,44,54),(1705,11,1,47,5,5,46,54),(1706,11,1,48,1,1,39,55),(1707,11,1,48,1,2,39,55),(1708,11,1,48,1,3,40,55),(1709,11,1,48,1,4,40,55),(1710,11,1,48,1,5,40,55),(1711,11,1,48,1,6,40,55),(1712,11,1,48,2,1,41,55),(1713,11,1,48,2,2,41,55),(1714,11,1,48,2,3,42,55),(1715,11,1,48,2,4,42,55),(1716,11,1,48,2,5,40,55),(1717,11,1,48,2,6,40,55),(1718,11,1,48,3,1,41,55),(1719,11,1,48,3,2,41,55),(1720,11,1,48,3,3,40,55),(1721,11,1,48,3,4,40,55),(1722,11,1,48,3,5,40,55),(1723,11,1,48,3,6,40,55),(1724,11,1,48,4,1,38,49),(1725,11,1,48,4,2,38,49),(1726,11,1,48,4,3,45,50),(1727,11,1,48,4,4,45,50),(1728,11,1,48,4,5,40,55),(1729,11,1,48,4,6,40,55),(1730,11,1,48,5,1,46,55),(1731,11,1,48,5,2,44,55),(1732,11,1,48,5,3,46,55),(1733,11,1,48,5,4,44,55),(1734,11,1,48,5,5,46,55);
/*!40000 ALTER TABLE `jadwal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jpperhari`
--

DROP TABLE IF EXISTS `jpperhari`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jpperhari` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  `hari` int(11) NOT NULL,
  `jp` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `JpPerHari_id_sekolah_id_kelas_hari_key` (`id_sekolah`,`id_kelas`,`hari`),
  KEY `JpPerHari_id_kelas_fkey` (`id_kelas`),
  CONSTRAINT `JpPerHari_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `JpPerHari_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jpperhari`
--

LOCK TABLES `jpperhari` WRITE;
/*!40000 ALTER TABLE `jpperhari` DISABLE KEYS */;
/*!40000 ALTER TABLE `jpperhari` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kelas`
--

DROP TABLE IF EXISTS `kelas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kelas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_tingkatan` int(11) NOT NULL,
  `nama_kelas` varchar(191) NOT NULL,
  `kode_lengkap` varchar(191) NOT NULL,
  `fase` varchar(191) DEFAULT NULL,
  `id_wali_kelas` int(11) DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `Kelas_id_sekolah_id_tingkatan_nama_kelas_key` (`id_sekolah`,`id_tingkatan`,`nama_kelas`),
  KEY `Kelas_id_tingkatan_fkey` (`id_tingkatan`),
  CONSTRAINT `Kelas_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `Kelas_id_tingkatan_fkey` FOREIGN KEY (`id_tingkatan`) REFERENCES `tingkatan` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kelas`
--

LOCK TABLES `kelas` WRITE;
/*!40000 ALTER TABLE `kelas` DISABLE KEYS */;
INSERT INTO `kelas` VALUES (37,11,19,'A','1A','Fase A',NULL,NULL,'2026-10-05 02:31:24.716'),(38,11,19,'B','1B','Fase A',NULL,NULL,'2026-10-05 02:31:24.728'),(39,11,20,'A','2A','Fase A',NULL,NULL,'2026-10-05 02:31:24.738'),(40,11,20,'B','2B','Fase A',NULL,NULL,'2026-10-05 02:31:24.777'),(41,11,21,'A','3A','Fase B',NULL,NULL,'2026-10-05 02:31:24.793'),(42,11,21,'B','3B','Fase B',NULL,NULL,'2026-10-05 02:31:24.811'),(43,11,22,'A','4A','Fase B',NULL,NULL,'2026-10-05 02:31:24.832'),(44,11,22,'B','4B','Fase B',NULL,NULL,'2026-10-05 02:31:24.845'),(45,11,23,'A','5A','Fase C',NULL,NULL,'2026-10-05 02:31:24.871'),(46,11,23,'B','5B','Fase C',NULL,NULL,'2026-10-05 02:31:24.876'),(47,11,24,'A','6A','Fase C',NULL,NULL,'2026-10-05 02:31:24.889'),(48,11,24,'B','6B','Fase C',NULL,NULL,'2026-10-05 02:31:24.903');
/*!40000 ALTER TABLE `kelas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mapel`
--

DROP TABLE IF EXISTS `mapel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mapel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `nama` varchar(191) NOT NULL,
  `prioritas` tinyint(1) NOT NULL DEFAULT 0,
  `color` varchar(191) NOT NULL DEFAULT '#E2E8F0',
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Mapel_id_sekolah_nama_key` (`id_sekolah`,`nama`),
  CONSTRAINT `Mapel_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mapel`
--

LOCK TABLES `mapel` WRITE;
/*!40000 ALTER TABLE `mapel` DISABLE KEYS */;
INSERT INTO `mapel` VALUES (38,11,'Pendidikan Agama & Budi Pekerti',0,'#3B82F6',NULL),(39,11,'Pendidikan Pancasila',0,'#10B981',NULL),(40,11,'Bahasa Indonesia',1,'#EF4444',NULL),(41,11,'Matematika',1,'#F59E0B',NULL),(42,11,'IPAS (Sains & Sosial)',1,'#8B5CF6',NULL),(43,11,'PJOK',0,'#06B6D4',NULL),(44,11,'Seni Rupa & Budaya',0,'#EC4899',NULL),(45,11,'Bahasa Inggris',0,'#6366F1',NULL),(46,11,'Muatan Lokal (Bahasa Sunda)',0,'#64748B',NULL);
/*!40000 ALTER TABLE `mapel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mapeltingkatan`
--

DROP TABLE IF EXISTS `mapeltingkatan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mapeltingkatan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_mapel` int(11) NOT NULL,
  `id_tingkatan` int(11) NOT NULL,
  `jp_per_minggu` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `MapelTingkatan_id_sekolah_id_mapel_id_tingkatan_key` (`id_sekolah`,`id_mapel`,`id_tingkatan`),
  KEY `MapelTingkatan_id_mapel_fkey` (`id_mapel`),
  KEY `MapelTingkatan_id_tingkatan_fkey` (`id_tingkatan`),
  CONSTRAINT `MapelTingkatan_id_mapel_fkey` FOREIGN KEY (`id_mapel`) REFERENCES `mapel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `MapelTingkatan_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `MapelTingkatan_id_tingkatan_fkey` FOREIGN KEY (`id_tingkatan`) REFERENCES `tingkatan` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mapeltingkatan`
--

LOCK TABLES `mapeltingkatan` WRITE;
/*!40000 ALTER TABLE `mapeltingkatan` DISABLE KEYS */;
INSERT INTO `mapeltingkatan` VALUES (157,11,38,19,4),(158,11,39,19,4),(159,11,40,19,8),(160,11,41,19,6),(161,11,43,19,3),(162,11,44,19,2),(163,11,46,19,2),(164,11,38,20,4),(165,11,39,20,4),(166,11,40,20,8),(167,11,41,20,6),(168,11,43,20,3),(169,11,44,20,2),(170,11,46,20,2),(171,11,38,21,4),(172,11,39,21,4),(173,11,40,21,6),(174,11,41,21,6),(175,11,42,21,5),(176,11,43,21,3),(177,11,44,21,2),(178,11,45,21,2),(179,11,46,21,2),(180,11,38,22,4),(181,11,39,22,4),(182,11,40,22,6),(183,11,41,22,6),(184,11,42,22,5),(185,11,43,22,3),(186,11,44,22,2),(187,11,45,22,2),(188,11,46,22,2),(189,11,38,23,3),(190,11,39,23,4),(191,11,40,23,6),(192,11,41,23,6),(193,11,42,23,5),(194,11,43,23,3),(195,11,44,23,2),(196,11,45,23,2),(197,11,46,23,2),(198,11,38,24,3),(199,11,39,24,4),(200,11,40,24,6),(201,11,41,24,6),(202,11,42,24,5),(203,11,43,24,3),(204,11,44,24,2),(205,11,45,24,2),(206,11,46,24,2);
/*!40000 ALTER TABLE `mapeltingkatan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pengampu`
--

DROP TABLE IF EXISTS `pengampu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pengampu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_guru` int(11) NOT NULL,
  `id_mapel` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Pengampu_id_sekolah_id_guru_id_mapel_id_kelas_key` (`id_sekolah`,`id_guru`,`id_mapel`,`id_kelas`),
  KEY `Pengampu_id_guru_fkey` (`id_guru`),
  KEY `Pengampu_id_mapel_fkey` (`id_mapel`),
  KEY `Pengampu_id_kelas_fkey` (`id_kelas`),
  CONSTRAINT `Pengampu_id_guru_fkey` FOREIGN KEY (`id_guru`) REFERENCES `guru` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Pengampu_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Pengampu_id_mapel_fkey` FOREIGN KEY (`id_mapel`) REFERENCES `mapel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Pengampu_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=541 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pengampu`
--

LOCK TABLES `pengampu` WRITE;
/*!40000 ALTER TABLE `pengampu` DISABLE KEYS */;
INSERT INTO `pengampu` VALUES (434,11,44,39,37),(435,11,44,40,37),(436,11,44,41,37),(437,11,44,42,37),(439,11,44,44,37),(441,11,44,46,37),(443,11,45,39,38),(444,11,45,40,38),(445,11,45,41,38),(446,11,45,42,38),(448,11,45,44,38),(450,11,45,46,38),(452,11,46,39,39),(453,11,46,40,39),(454,11,46,41,39),(455,11,46,42,39),(457,11,46,44,39),(459,11,46,46,39),(461,11,47,39,40),(462,11,47,40,40),(463,11,47,41,40),(464,11,47,42,40),(466,11,47,44,40),(468,11,47,46,40),(470,11,48,39,41),(471,11,48,40,41),(472,11,48,41,41),(473,11,48,42,41),(438,11,48,43,37),(447,11,48,43,38),(456,11,48,43,39),(465,11,48,43,40),(474,11,48,43,41),(483,11,48,43,42),(492,11,48,43,43),(501,11,48,43,44),(510,11,48,43,45),(519,11,48,43,46),(528,11,48,43,47),(537,11,48,43,48),(475,11,48,44,41),(477,11,48,46,41),(433,11,49,38,37),(442,11,49,38,38),(451,11,49,38,39),(460,11,49,38,40),(469,11,49,38,41),(478,11,49,38,42),(487,11,49,38,43),(496,11,49,38,44),(505,11,49,38,45),(514,11,49,38,46),(523,11,49,38,47),(532,11,49,38,48),(479,11,49,39,42),(480,11,49,40,42),(481,11,49,41,42),(482,11,49,42,42),(484,11,49,44,42),(486,11,49,46,42),(488,11,50,39,43),(489,11,50,40,43),(490,11,50,41,43),(491,11,50,42,43),(493,11,50,44,43),(440,11,50,45,37),(449,11,50,45,38),(458,11,50,45,39),(467,11,50,45,40),(476,11,50,45,41),(485,11,50,45,42),(494,11,50,45,43),(503,11,50,45,44),(512,11,50,45,45),(521,11,50,45,46),(530,11,50,45,47),(539,11,50,45,48),(495,11,50,46,43),(497,11,51,39,44),(498,11,51,40,44),(499,11,51,41,44),(500,11,51,42,44),(502,11,51,44,44),(504,11,51,46,44),(506,11,52,39,45),(507,11,52,40,45),(508,11,52,41,45),(509,11,52,42,45),(511,11,52,44,45),(513,11,52,46,45),(515,11,53,39,46),(516,11,53,40,46),(517,11,53,41,46),(518,11,53,42,46),(520,11,53,44,46),(522,11,53,46,46),(524,11,54,39,47),(525,11,54,40,47),(526,11,54,41,47),(527,11,54,42,47),(529,11,54,44,47),(531,11,54,46,47),(533,11,55,39,48),(534,11,55,40,48),(535,11,55,41,48),(536,11,55,42,48),(538,11,55,44,48),(540,11,55,46,48);
/*!40000 ALTER TABLE `pengampu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `periodejadwal`
--

DROP TABLE IF EXISTS `periodejadwal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `periodejadwal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `nama` varchar(191) NOT NULL,
  `tahun_ajaran` varchar(191) DEFAULT NULL,
  `semester` varchar(191) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `PeriodeJadwal_id_sekolah_idx` (`id_sekolah`),
  CONSTRAINT `PeriodeJadwal_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `periodejadwal`
--

LOCK TABLES `periodejadwal` WRITE;
/*!40000 ALTER TABLE `periodejadwal` DISABLE KEYS */;
INSERT INTO `periodejadwal` VALUES (1,11,'Tahun Ajaran 2026/2027 (Semester Ganjil - Terbit)','2026/2027','GASAL',1,'2026-10-05 02:31:26.698','2026-10-05 02:31:26.698'),(2,11,'Tahun Ajaran 2025/2026 (Semester Genap - Arsip)','2025/2026','GENAP',0,'2026-10-05 02:31:26.712','2026-10-05 02:31:26.712'),(3,11,'Tahun Ajaran 2026/2027 (Semester Genap - Draf Perencanaan)','2026/2027','GENAP',0,'2026-10-05 02:31:26.718','2026-10-05 02:31:26.718');
/*!40000 ALTER TABLE `periodejadwal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `routineactivity`
--

DROP TABLE IF EXISTS `routineactivity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `routineactivity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `name` varchar(191) NOT NULL,
  `day_of_week` int(11) DEFAULT NULL,
  `time_before_jp` int(11) DEFAULT 1,
  `duration` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `RoutineActivity_id_sekolah_fkey` (`id_sekolah`),
  CONSTRAINT `RoutineActivity_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `routineactivity`
--

LOCK TABLES `routineactivity` WRITE;
/*!40000 ALTER TABLE `routineactivity` DISABLE KEYS */;
INSERT INTO `routineactivity` VALUES (39,11,'Upacara Bendera',1,1,35,1),(40,11,'Pembiasaan Literasi & Numerasi Pagi',2,1,15,1),(41,11,'Senam Kesegaran Jasmani / Sholat Dhuha',5,1,30,1),(42,11,'Istirahat I & Kudapan Sehat',NULL,4,15,1),(43,11,'Istirahat II & Sholat Dzuhur Berjamaah',NULL,6,20,1);
/*!40000 ALTER TABLE `routineactivity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduleslot`
--

DROP TABLE IF EXISTS `scheduleslot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scheduleslot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  `hari` int(11) NOT NULL,
  `jam_ke` int(11) NOT NULL,
  `waktu_mulai` datetime(3) NOT NULL,
  `waktu_selesai` datetime(3) NOT NULL,
  `is_break` tinyint(1) NOT NULL DEFAULT 0,
  `is_ceremony` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ScheduleSlot_id_sekolah_id_kelas_hari_jam_ke_key` (`id_sekolah`,`id_kelas`,`hari`,`jam_ke`),
  KEY `ScheduleSlot_id_kelas_fkey` (`id_kelas`),
  CONSTRAINT `ScheduleSlot_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ScheduleSlot_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduleslot`
--

LOCK TABLES `scheduleslot` WRITE;
/*!40000 ALTER TABLE `scheduleslot` DISABLE KEYS */;
/*!40000 ALTER TABLE `scheduleslot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schoolconfig`
--

DROP TABLE IF EXISTS `schoolconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schoolconfig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `is_parallel` tinyint(1) NOT NULL DEFAULT 0,
  `class_naming` varchar(191) NOT NULL DEFAULT 'alphabet',
  `school_days` int(11) NOT NULL DEFAULT 5,
  `start_time` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `duration_per_jp` int(11) NOT NULL DEFAULT 35,
  `has_routine` tinyint(1) NOT NULL DEFAULT 0,
  `routine_duration` int(11) NOT NULL DEFAULT 15,
  `break1_duration` int(11) NOT NULL DEFAULT 15,
  `break2_duration` int(11) NOT NULL DEFAULT 15,
  `break1_after_jp` int(11) NOT NULL DEFAULT 3,
  `break2_after_jp` int(11) NOT NULL DEFAULT 5,
  `has_monday_ceremony` tinyint(1) NOT NULL DEFAULT 1,
  `ceremony_duration` int(11) NOT NULL DEFAULT 35,
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `SchoolConfig_id_sekolah_key` (`id_sekolah`),
  CONSTRAINT `SchoolConfig_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schoolconfig`
--

LOCK TABLES `schoolconfig` WRITE;
/*!40000 ALTER TABLE `schoolconfig` DISABLE KEYS */;
INSERT INTO `schoolconfig` VALUES (9,11,1,'alphabet',5,'1970-01-01 07:00:00.000',35,1,15,15,15,3,5,1,35,'2026-10-05 02:31:23.898'),(10,12,0,'number',6,'1970-01-01 07:15:00.000',35,1,15,20,15,3,5,1,35,'2026-10-05 02:31:23.930');
/*!40000 ALTER TABLE `schoolconfig` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sekolah`
--

DROP TABLE IF EXISTS `sekolah`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sekolah` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npsn` varchar(191) NOT NULL,
  `nama_sekolah` varchar(191) NOT NULL,
  `alamat` varchar(191) DEFAULT NULL,
  `jenjang` varchar(191) DEFAULT 'SD',
  `kepala_sekolah` varchar(191) DEFAULT NULL,
  `semester_aktif` varchar(191) DEFAULT 'GASAL',
  `tahun_pelajaran` varchar(191) DEFAULT '2026/2027',
  `setupStep` int(11) NOT NULL DEFAULT 1,
  `setupCompleted` tinyint(1) NOT NULL DEFAULT 0,
  `status` varchar(191) NOT NULL DEFAULT 'PENDING_SETUP',
  `deleted_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Sekolah_npsn_key` (`npsn`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sekolah`
--

LOCK TABLES `sekolah` WRITE;
/*!40000 ALTER TABLE `sekolah` DISABLE KEYS */;
INSERT INTO `sekolah` VALUES (11,'20108922','SDN Pancasila 01','Jl. Percetakan Negara No. 21, Cempaka Putih, Jakarta Pusat','SD','Drs. H. Subagyo, M.M.','GASAL','2026/2027',1,1,'ACTIVE',NULL,'2026-10-05 02:31:23.898','2026-10-05 02:31:23.898'),(12,'20104011','SDN Teladan 05','Jl. Salemba Raya No. 14, Senen, Jakarta Pusat','SD','Hj. Maryati, M.Pd.','GASAL','2026/2027',1,1,'ACTIVE',NULL,'2026-10-05 02:31:23.930','2026-10-05 02:31:23.930'),(13,'20103450','SDN Cibubur 03','Jl. Lapangan Tembak No. 12, Ciracas, Jakarta Timur','SD','Dra. Endang S., M.Pd.','GASAL','2026/2027',1,0,'PENDING_VERIFICATION',NULL,'2026-10-05 02:31:23.957','2026-10-05 02:31:23.957'),(14,'20109931','SD Harapan Mandiri','Jl. Rawamangun Muka No. 9, Jakarta Timur','SD','Ibu Yuliana, S.Pd.','GASAL','2026/2027',1,0,'REJECTED',NULL,'2026-10-05 02:31:23.989','2026-10-05 02:31:23.989');
/*!40000 ALTER TABLE `sekolah` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharedlink`
--

DROP TABLE IF EXISTS `sharedlink`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharedlink` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `uuid` varchar(191) NOT NULL,
  `id_kelas` int(11) DEFAULT NULL,
  `permission` varchar(191) NOT NULL DEFAULT 'read',
  `created_by` int(11) NOT NULL,
  `allowed_user_id` int(11) DEFAULT NULL,
  `expires_at` datetime(3) DEFAULT NULL,
  `view_count` int(11) NOT NULL DEFAULT 0,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `SharedLink_uuid_key` (`uuid`),
  KEY `SharedLink_uuid_idx` (`uuid`),
  KEY `SharedLink_expires_at_idx` (`expires_at`),
  KEY `SharedLink_id_sekolah_fkey` (`id_sekolah`),
  KEY `SharedLink_id_kelas_fkey` (`id_kelas`),
  KEY `SharedLink_created_by_fkey` (`created_by`),
  KEY `SharedLink_allowed_user_id_fkey` (`allowed_user_id`),
  CONSTRAINT `SharedLink_allowed_user_id_fkey` FOREIGN KEY (`allowed_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SharedLink_created_by_fkey` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SharedLink_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SharedLink_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharedlink`
--

LOCK TABLES `sharedlink` WRITE;
/*!40000 ALTER TABLE `sharedlink` DISABLE KEYS */;
INSERT INTO `sharedlink` VALUES (9,11,'link-view-kelas-3a-aktif',41,'read',73,NULL,'2026-11-04 02:31:26.790',58,'2026-10-05 02:31:26.793'),(10,11,'link-view-semester-lalu-expired',41,'read',73,NULL,'2026-09-28 02:31:26.797',142,'2026-10-05 02:31:26.800');
/*!40000 ALTER TABLE `sharedlink` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) NOT NULL,
  `thumbnail` varchar(191) DEFAULT NULL,
  `css_styles` varchar(191) NOT NULL,
  `is_premium` tinyint(1) NOT NULL DEFAULT 0,
  `created_by` int(11) NOT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Template_created_by_fkey` (`created_by`),
  CONSTRAINT `Template_created_by_fkey` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (4,'Template Standar Bersih (Format Cetak Kemdikbud)','template_clean_standard.png','body { font-family: Inter; } table { border: 1px solid #CBD5E1; }',0,67,'2026-10-05 02:31:26.808','2026-10-05 02:31:26.808'),(5,'Superhero Ceria & Edukatif (Kelas Bawah SD)','template_superhero.png','body { font-family: Comic; background: #FEF2F2; } th { background: #EF4444; }',1,67,'2026-10-05 02:31:26.808','2026-10-05 02:31:26.808'),(6,'Flora Tropis & Satwa Nusantara','template_botanical.png','body { font-family: Outfit; background: #F0FDF4; } th { background: #15803D; }',1,67,'2026-10-05 02:31:26.808','2026-10-05 02:31:26.808');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tingkatan`
--

DROP TABLE IF EXISTS `tingkatan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tingkatan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `nama` varchar(191) NOT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Tingkatan_id_sekolah_nama_key` (`id_sekolah`,`nama`),
  CONSTRAINT `Tingkatan_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tingkatan`
--

LOCK TABLES `tingkatan` WRITE;
/*!40000 ALTER TABLE `tingkatan` DISABLE KEYS */;
INSERT INTO `tingkatan` VALUES (19,11,'Kelas 1',NULL),(20,11,'Kelas 2',NULL),(21,11,'Kelas 3',NULL),(22,11,'Kelas 4',NULL),(23,11,'Kelas 5',NULL),(24,11,'Kelas 6',NULL);
/*!40000 ALTER TABLE `tingkatan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) DEFAULT NULL,
  `id_guru` int(11) DEFAULT NULL,
  `role` varchar(191) NOT NULL DEFAULT 'ADMIN_SEKOLAH',
  `nama` varchar(191) NOT NULL,
  `email` varchar(191) NOT NULL,
  `no_hp` varchar(191) DEFAULT NULL,
  `password` varchar(191) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `is_verified` tinyint(1) NOT NULL DEFAULT 1,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0,
  `deleted_at` datetime(3) DEFAULT NULL,
  `last_login` datetime(3) DEFAULT NULL,
  `reset_token` varchar(191) DEFAULT NULL,
  `reset_token_expires` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) NOT NULL DEFAULT current_timestamp(3),
  `updated_at` datetime(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `User_email_key` (`email`),
  KEY `User_id_sekolah_idx` (`id_sekolah`),
  KEY `User_email_idx` (`email`),
  KEY `User_id_guru_fkey` (`id_guru`),
  CONSTRAINT `User_id_guru_fkey` FOREIGN KEY (`id_guru`) REFERENCES `guru` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `User_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (66,NULL,NULL,'SUPER_ADMIN','Super Administrator Sistem','superadmin@jadwale.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.851','2026-10-05 02:31:23.851'),(67,NULL,NULL,'DESIGNER','Studio Desain Kreatif Jadwale','designer.kreatif@jadwale.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.886','2026-10-05 02:31:23.886'),(68,11,NULL,'ADMIN_SEKOLAH','Admin Kurikulum SDN Pancasila 01','admin@sdnpancasila01.sch.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.909','2026-10-05 02:31:23.909'),(69,11,NULL,'ADMIN_SEKOLAH','Operator SDN Pancasila 01','operator@sdnpancasila01.sch.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.919','2026-10-05 02:31:23.919'),(70,12,NULL,'ADMIN_SEKOLAH','Admin SDN Teladan 05','admin@sdnteladan05.sch.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.947','2026-10-05 02:31:23.947'),(71,13,NULL,'ADMIN_SEKOLAH','Admin SDN Cibubur 03','admin@sdncibubur03.sch.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,0,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:23.964','2026-10-05 02:31:23.964'),(72,14,NULL,'ADMIN_SEKOLAH','Admin SD Harapan Mandiri','admin@sdharapanmandiri.sch.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',0,0,1,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.000','2026-10-05 02:31:24.000'),(73,11,44,'TENAGA_PENDIDIK','Bambang Sutrisno, M.Pd.','bambang.sutrisno@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.048','2026-10-05 02:31:24.048'),(74,11,45,'TENAGA_PENDIDIK','Siti Aminah, S.Pd.','siti.aminah@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.088','2026-10-05 02:31:24.088'),(75,11,46,'TENAGA_PENDIDIK','Ahmad Fauzi, S.Pd.','ahmad.fauzi@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.142','2026-10-05 02:31:24.142'),(76,11,47,'TENAGA_PENDIDIK','Dewi Lestari, S.Pd.','dewi.lestari@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.183','2026-10-05 02:31:24.183'),(77,11,48,'TENAGA_PENDIDIK','Hendra Gunawan, S.Pd.','hendra.gunawan@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.220','2026-10-05 02:31:24.220'),(78,11,49,'TENAGA_PENDIDIK','Rina Oktavia, S.Pd.I','rina.oktavia@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.352','2026-10-05 02:31:24.352'),(79,11,50,'TENAGA_PENDIDIK','Wahyu Setiawan, S.Pd.','wahyu.setiawan@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.500','2026-10-05 02:31:24.500'),(80,11,51,'TENAGA_PENDIDIK','Nurul Hidayah, S.Pd.','nurul.hidayah@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.519','2026-10-05 02:31:24.519'),(81,11,52,'TENAGA_PENDIDIK','Agus Prasetyo, S.Pd.','agus.prasetyo@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.541','2026-10-05 02:31:24.541'),(82,11,53,'TENAGA_PENDIDIK','Maya Sari, S.Pd.','maya.sari@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.562','2026-10-05 02:31:24.562'),(83,11,54,'TENAGA_PENDIDIK','Doni Firmansyah, S.Pd.','doni.firmansyah@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.577','2026-10-05 02:31:24.577'),(84,11,55,'TENAGA_PENDIDIK','Sri Wulandari, S.Pd.','sri.wulandari@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.588','2026-10-05 02:31:24.588'),(85,11,56,'TENAGA_PENDIDIK','Ratna Kartika, S.Pd.','ratna.kartika@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',1,0,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.605','2026-10-05 02:31:24.605'),(86,11,57,'TENAGA_PENDIDIK','Joko Widodo, S.Pd.','joko.widodo@guru.sd.belajar.id',NULL,'$2b$10$yj4UPDJehSxjWjmrfahiHO0UWdCD9wMtNH8TAWM2mW4MXasgviJny',0,1,0,NULL,NULL,NULL,NULL,'2026-10-05 02:31:24.626','2026-10-05 02:31:24.626');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `walikelas`
--

DROP TABLE IF EXISTS `walikelas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `walikelas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sekolah` int(11) NOT NULL,
  `id_guru` int(11) NOT NULL,
  `id_kelas` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `WaliKelas_id_sekolah_id_guru_key` (`id_sekolah`,`id_guru`),
  UNIQUE KEY `WaliKelas_id_sekolah_id_kelas_key` (`id_sekolah`,`id_kelas`),
  KEY `WaliKelas_id_guru_fkey` (`id_guru`),
  KEY `WaliKelas_id_kelas_fkey` (`id_kelas`),
  CONSTRAINT `WaliKelas_id_guru_fkey` FOREIGN KEY (`id_guru`) REFERENCES `guru` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `WaliKelas_id_kelas_fkey` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `WaliKelas_id_sekolah_fkey` FOREIGN KEY (`id_sekolah`) REFERENCES `sekolah` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `walikelas`
--

LOCK TABLES `walikelas` WRITE;
/*!40000 ALTER TABLE `walikelas` DISABLE KEYS */;
INSERT INTO `walikelas` VALUES (37,11,44,37),(38,11,45,38),(39,11,46,39),(40,11,47,40),(41,11,48,41),(42,11,49,42),(43,11,50,43),(44,11,51,44),(45,11,52,45),(46,11,53,46),(47,11,54,47),(48,11,55,48);
/*!40000 ALTER TABLE `walikelas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-05  9:31:34
