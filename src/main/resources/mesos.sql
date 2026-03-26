--
-- Database: `mesos`
--

-- --------------------------------------------------------

--
-- Table structure for table `building_cards`
--

CREATE TABLE `building_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `base_prestige` int DEFAULT NULL,
  `cost` int DEFAULT NULL,
  `effect_code` varchar(50) DEFAULT NULL,
  `effect_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `building_cards`
--

INSERT INTO `building_cards` (`id`, `era`, `base_prestige`, `cost`, `effect_code`, `effect_params`) VALUES
('build_01', 1, 3, 5, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"ARTIST\", \"discountPerCharacter\": 1}'),
('build_02', 1, 4, 4, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"GATHERER\", \"discountPerCharacter\": 1}'),
('build_03', 2, 4, 7, 'SUSTENANCE_DISCOUNT_PER_CHARACTER', '{\"targetCharacter\": \"INVENTOR\", \"discountPerCharacter\": 1}'),
('build_04', 1, 2, 5, 'SHAMANIC_PENALTY_IMMUNITY', '{}'),
('build_05', 1, 3, 3, 'EXTRA_FOOD_ON_TOTEM', '{\"extraFood\": 1}'),
('build_06', 1, 4, 3, 'FOOD_PER_INVENTOR_PAIR', '{\"extraFood\": 3}'),
('build_07', 2, 0, 7, 'SHAMANIC_DOUBLE_REWARD', '{}'),
('build_08', 2, 4, 6, 'EXTRA_SHAMAN_STARS', '{\"extraStars\": 3}'),
('build_09', 2, 6, 5, 'EXTRA_RESOURCES_ON_EVENT', '{\"extraFood\": 1, \"targetEvent\": \"CAVE_PAINTINGS\", \"extraPrestige\": 0, \"targetCharacter\": \"ARTIST\"}'),
('build_10', 2, 2, 7, 'EXTRA_RESOURCES_ON_EVENT', '{\"extraFood\": 1, \"targetEvent\": \"HUNT\", \"extraPrestige\": 1, \"targetCharacter\": \"HUNTER\"}'),
('build_11', 2, 4, 6, 'ENDGAME_BUILDER_PRESTIGE_MULTIPLIER', '{\"multiplier\": 2}'),
('build_12', 1, 3, 4, 'FOOD_PER_SET', '{\"extraFood\": 5}'),
('build_13', 2, 6, 5, 'ENDGAME_PRESTIGE_PER_SET', '{\"pointsPerSet\": 6}'),
('build_14', 3, 8, 8, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"HUNTER\", \"pointsPerCharacter\": 3}'),
('build_15', 3, 6, 6, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"INVENTOR\", \"pointsPerCharacter\": 2}'),
('build_16', 3, 4, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"ARTIST\", \"pointsPerCharacter\": 4}'),
('build_17', 3, 4, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"SHAMAN\", \"pointsPerCharacter\": 4}'),
('build_18', 3, 6, 7, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"GATHERER\", \"pointsPerCharacter\": 4}'),
('build_19', 3, 3, 6, 'ENDGAME_PRESTIGE_PER_CHARACTER', '{\"targetCharacter\": \"BUILDER\", \"pointsPerCharacter\": 4}'),
('build_20', 3, 3, 9, 'EXTRA_CARD_ON_ROUND_END', '{}'),
('build_21', 3, 25, 10, 'NO_EFFECT', '{}');

-- --------------------------------------------------------

--
-- Table structure for table `character_cards`
--

CREATE TABLE `character_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `base_prestige` int DEFAULT NULL,
  `min_players` int DEFAULT NULL,
  `character_type` varchar(20) DEFAULT NULL,
  `character_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `character_cards`
--

INSERT INTO `character_cards` (`id`, `era`, `base_prestige`, `min_players`, `character_type`, `character_params`) VALUES
('char_01', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_02', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_03', 3, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_04', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_05', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_06', 2, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_07', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_08', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_09', 1, 0, 0, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_10', 2, 0, 3, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_11', 1, 0, 3, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_12', 1, 0, 4, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_13', 3, 0, 5, 'ARTIST', '{\"art_prestige_per_pair\": 10}'),
('char_14', 1, 2, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_15', 1, 3, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_16', 1, 0, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_17', 1, 1, 5, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_18', 2, 2, 3, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_19', 2, 1, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_20', 2, 4, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_21', 2, 3, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_22', 3, 3, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_23', 3, 2, 0, 'BUILDER', '{\"bld_building_discount\": 2}'),
('char_24', 3, 5, 0, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_25', 3, 4, 5, 'BUILDER', '{\"bld_building_discount\": 1}'),
('char_26', 1, 0, 3, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_27', 1, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_28', 1, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_29', 1, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_30', 2, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_31', 2, 0, 4, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_32', 2, 0, 3, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_33', 2, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_34', 3, 0, 5, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_35', 3, 0, 4, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_36', 3, 0, 0, 'GATHERER', '{\"gat_sustenance_discount\": 3}'),
('char_37', 1, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_38', 1, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_39', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_40', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_41', 1, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_42', 2, 0, 5, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_43', 2, 0, 4, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_44', 2, 0, 3, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_45', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_46', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_47', 2, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_48', 3, 0, 5, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_49', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 1}'),
('char_50', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_51', 3, 0, 0, 'HUNTER', '{\"hnt_has_meat\": 0}'),
('char_52', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"ROPE\"}'),
('char_53', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"FLUTE\"}'),
('char_54', 1, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"POTTERY\"}'),
('char_55', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"CANOE\"}'),
('char_56', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"ARROWHEAD\"}'),
('char_57', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"LEATHER\"}'),
('char_58', 1, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"BREAD\"}'),
('char_59', 2, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"FISHHOOK\"}'),
('char_60', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"IDOL\"}'),
('char_61', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"LEATHER\"}'),
('char_62', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"POTTERY\"}'),
('char_63', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"FLUTE\"}'),
('char_64', 2, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"ROPE\"}'),
('char_65', 3, 0, 4, 'INVENTOR', '{\"inv_invention_type\": \"NECKLACE\"}'),
('char_66', 3, 0, 3, 'INVENTOR', '{\"inv_invention_type\": \"CANOE\"}'),
('char_67', 3, 0, 3, 'INVENTOR', '{\"inv_invention_type\": \"ARROWHEAD\"}'),
('char_68', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"BREAD\"}'),
('char_69', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"NECKLACE\"}'),
('char_70', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"FISHHOOK\"}'),
('char_71', 3, 0, 0, 'INVENTOR', '{\"inv_invention_type\": \"IDOL\"}'),
('char_72', 1, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_73', 1, 0, 4, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('char_74', 1, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_75', 1, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('char_76', 2, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_77', 2, 0, 5, 'SHAMAN', '{\"sha_shaman_stars\": 1}'),
('char_78', 2, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_79', 2, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_80', 3, 0, 4, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_81', 3, 0, 3, 'SHAMAN', '{\"sha_shaman_stars\": 2}'),
('char_82', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 3}'),
('char_83', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 3}'),
('char_84', 3, 0, 0, 'SHAMAN', '{\"sha_shaman_stars\": 2}');

-- --------------------------------------------------------

--
-- Table structure for table `event_cards`
--

CREATE TABLE `event_cards` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `era` int DEFAULT NULL,
  `event_type` varchar(20) DEFAULT NULL,
  `event_params` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `event_cards`
--

INSERT INTO `event_cards` (`id`, `era`, `event_type`, `event_params`) VALUES
('event_01', 1, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 1, \"cave_bonus_threshold\": 1, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 0}'),
('event_02', 2, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 2, \"cave_bonus_threshold\": 2, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 1}'),
('event_03', 3, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 3, \"cave_bonus_threshold\": 3, \"cave_prestige_penalty\": 2, \"cave_penalty_threshold\": 2}'),
('event_04', 1, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 1}'),
('event_05', 2, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 2}'),
('event_06', 3, 'HUNT', '{\"hunt_food_bonus\": 1, \"hunt_prestige_bonus\": 3}'),
('event_07', 1, 'SHAMANIC_RITUAL', '{\"sham_prestige_bonus\": 5, \"sham_prestige_penalty\": 3}'),
('event_08', 2, 'SHAMANIC_RITUAL', '{\"sham_prestige_bonus\": 10, \"sham_prestige_penalty\": 5}'),
('event_09', 3, 'SHAMANIC_RITUAL', '{\"is_final\": true, \"sham_prestige_bonus\": 15, \"sham_prestige_penalty\": 7}'),
('event_10', 1, 'SUSTENANCE', '{\"sust_food_cost\": 1, \"sust_prestige_penalty\": 1}'),
('event_11', 2, 'SUSTENANCE', '{\"sust_food_cost\": 1, \"sust_prestige_penalty\": 2}'),
('event_12', 3, 'SUSTENANCE', '{\"is_final\": true, \"sust_food_cost\": 1, \"sust_prestige_penalty\": 3}');

-- --------------------------------------------------------

--
-- Table structure for table `offer_tiles`
--

CREATE TABLE `offer_tiles` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `min_players` int DEFAULT NULL,
  `act_draws_upper` int DEFAULT NULL,
  `act_draws_lower` int DEFAULT NULL,
  `act_food_bonus` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `offer_tiles`
--

INSERT INTO `offer_tiles` (`id`, `min_players`, `act_draws_upper`, `act_draws_lower`, `act_food_bonus`) VALUES
('offertile_A', 5, 0, 0, 3),
('offertile_B', 0, 0, 1, 0),
('offertile_C', 0, 1, 0, 0),
('offertile_D', 3, 0, 2, 0),
('offertile_E', 0, 1, 1, 0),
('offertile_F', 0, 2, 0, 0),
('offertile_G', 4, 2, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `turn_order_tiles`
--

CREATE TABLE `turn_order_tiles` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `required_players` int DEFAULT NULL,
  `food_modifiers` json DEFAULT NULL,
  `prestige_modifiers` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `turn_order_tiles`
--

INSERT INTO `turn_order_tiles` (`id`, `required_players`, `food_modifiers`, `prestige_modifiers`) VALUES
('ordertile_2', 2, '[1, -1]', '[0, -2]'),
('ordertile_3', 3, '[2, 0, -1]', '[0, 0, -2]'),
('ordertile_4', 4, '[2, 1, 0, -1]', '[0, 0, 0, -2]'),
('ordertile_5', 5, '[3, 1, 0, 0, -1]', '[0, 0, 0, 0, -2]');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `building_cards`
--
ALTER TABLE `building_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `character_cards`
--
ALTER TABLE `character_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `event_cards`
--
ALTER TABLE `event_cards`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `offer_tiles`
--
ALTER TABLE `offer_tiles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `turn_order_tiles`
--
ALTER TABLE `turn_order_tiles`
  ADD PRIMARY KEY (`id`);
COMMIT;
