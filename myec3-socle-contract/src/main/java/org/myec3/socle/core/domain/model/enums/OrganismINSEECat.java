/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;


/**
 * Enum that contains a list of organism INSEE code categories.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@XmlEnum
public enum OrganismINSEECat implements StructureINSEECat {
	@XmlEnumValue("1") _1("1"),
	@XmlEnumValue("1.1") _1_1("1.1"),
	@XmlEnumValue("1.2") _1_2("1.2"),
	@XmlEnumValue("1.3") _1_3("1.3"),
	@XmlEnumValue("1.4") _1_4("1.4"),
	@XmlEnumValue("1.5") _1_5("1.5"),
	@XmlEnumValue("1.6") _1_6("1.6"),
	@XmlEnumValue("1.7") _1_7("1.7"),
	@XmlEnumValue("1.8") _1_8("1.8"),
	@XmlEnumValue("1.9") _1_9("1.9"),
	@XmlEnumValue("2") _2("2"),
	@XmlEnumValue("2.1") _2_1("2.1"),
	@XmlEnumValue("2.1.10") _2_1_10("2.1.10"),
	@XmlEnumValue("2.1.20") _2_1_20("2.1.20"),
	@XmlEnumValue("2.2") _2_2("2.2"),
	@XmlEnumValue("2.2.10") _2_2_10("2.2.10"),
	@XmlEnumValue("2.2.20") _2_2_20("2.2.20"),
	@XmlEnumValue("2.3") _2_3("2.3"),
	@XmlEnumValue("2.3.10") _2_3_10("2.3.10"),
	@XmlEnumValue("2.3.20") _2_3_20("2.3.20"),
	@XmlEnumValue("2.3.85") _2_3_85("2.3.85"),
	@XmlEnumValue("2.4") _2_4("2.4"),
	@XmlEnumValue("2.7") _2_7("2.7"),
	@XmlEnumValue("2.9") _2_9("2.9"),
	@XmlEnumValue("3") _3("3"),
	@XmlEnumValue("3.1") _3_1("3.1"),
	@XmlEnumValue("3.1.10") _3_1_10("3.1.10"),
	@XmlEnumValue("3.1.20") _3_1_20("3.1.20"),
	@XmlEnumValue("3.2") _3_2("3.2"),
	@XmlEnumValue("3.2.05") _3_2_05("3.2.05"),
	@XmlEnumValue("3.2.10") _3_2_10("3.2.10"),
	@XmlEnumValue("3.2.20") _3_2_20("3.2.20"),
	@XmlEnumValue("3.2.90") _3_2_90("3.2.90"),
	@XmlEnumValue("4") _4("4"),
	@XmlEnumValue("4.1") _4_1("4.1"),
	@XmlEnumValue("4.1.10") _4_1_10("4.1.10"),
	@XmlEnumValue("4.1.20") _4_1_20("4.1.20"),
	@XmlEnumValue("4.1.30") _4_1_30("4.1.30"),
	@XmlEnumValue("4.1.40") _4_1_40("4.1.40"),
	@XmlEnumValue("4.1.50") _4_1_50("4.1.50"),
	@XmlEnumValue("4.1.60") _4_1_60("4.1.60"),
	@XmlEnumValue("5") _5("5"),
	@XmlEnumValue("5.1") _5_1("5.1"),
	@XmlEnumValue("5.1.91") _5_1_91("5.1.91"),
	@XmlEnumValue("5.1.92") _5_1_92("5.1.92"),
	@XmlEnumValue("5.1.93") _5_1_93("5.1.93"),
	@XmlEnumValue("5.1.94") _5_1_94("5.1.94"),
	@XmlEnumValue("5.1.95") _5_1_95("5.1.95"),
	@XmlEnumValue("5.1.96") _5_1_96("5.1.96"),
	@XmlEnumValue("5.2") _5_2("5.2"),
	@XmlEnumValue("5.2.02") _5_2_02("5.2.02"),
	@XmlEnumValue("5.2.03") _5_2_03("5.2.03"),
	@XmlEnumValue("5.3") _5_3("5.3"),
	@XmlEnumValue("5.3.06") _5_3_06("5.3.06"),
	@XmlEnumValue("5.3.07") _5_3_07("5.3.07"),
	@XmlEnumValue("5.3.08") _5_3_08("5.3.08"),
	@XmlEnumValue("5.3.09") _5_3_09("5.3.09"),
	@XmlEnumValue("5.3.70") _5_3_70("5.3.70"),
	@XmlEnumValue("5.3.85") _5_3_85("5.3.85"),
	@XmlEnumValue("5.4") _5_4("5.4"),
	@XmlEnumValue("5.4.10") _5_4_10("5.4.10"),
	@XmlEnumValue("5.4.15") _5_4_15("5.4.15"),
	@XmlEnumValue("5.4.22") _5_4_22("5.4.22"),
	@XmlEnumValue("5.4.26") _5_4_26("5.4.26"),
	@XmlEnumValue("5.4.30") _5_4_30("5.4.30"),
	@XmlEnumValue("5.4.31") _5_4_31("5.4.31"),
	@XmlEnumValue("5.4.32") _5_4_32("5.4.32"),
	@XmlEnumValue("5.4.42") _5_4_42("5.4.42"),
	@XmlEnumValue("5.4.43") _5_4_43("5.4.43"),
	@XmlEnumValue("5.4.51") _5_4_51("5.4.51"),
	@XmlEnumValue("5.4.53") _5_4_53("5.4.53"),
	@XmlEnumValue("5.4.54") _5_4_54("5.4.54"),
	@XmlEnumValue("5.4.55") _5_4_55("5.4.55"),
	@XmlEnumValue("5.4.58") _5_4_58("5.4.58"),
	@XmlEnumValue("5.4.59") _5_4_59("5.4.59"),
	@XmlEnumValue("5.4.60") _5_4_60("5.4.60"),
	@XmlEnumValue("5.4.70") _5_4_70("5.4.70"),
	@XmlEnumValue("5.4.85") _5_4_85("5.4.85"),
	@XmlEnumValue("5.4.98") _5_4_98("5.4.98"),
	@XmlEnumValue("5.4.99") _5_4_99("5.4.99"),
	@XmlEnumValue("5.5") _5_5("5.5"),
	@XmlEnumValue("5.5.05") _5_5_05("5.5.05"),
	@XmlEnumValue("5.5.10") _5_5_10("5.5.10"),
	@XmlEnumValue("5.5.15") _5_5_15("5.5.15"),
	@XmlEnumValue("5.5.20") _5_5_20("5.5.20"),
	@XmlEnumValue("5.5.22") _5_5_22("5.5.22"),
	@XmlEnumValue("5.5.25") _5_5_25("5.5.25"),
	@XmlEnumValue("5.5.30") _5_5_30("5.5.30"),
	@XmlEnumValue("5.5.31") _5_5_31("5.5.31"),
	@XmlEnumValue("5.5.32") _5_5_32("5.5.32"),
	@XmlEnumValue("5.5.42") _5_5_42("5.5.42"),
	@XmlEnumValue("5.5.43") _5_5_43("5.5.43"),
	@XmlEnumValue("5.5.46") _5_5_46("5.5.46"),
	@XmlEnumValue("5.5.47") _5_5_47("5.5.47"),
	@XmlEnumValue("5.5.48") _5_5_48("5.5.48"),
	@XmlEnumValue("5.5.51") _5_5_51("5.5.51"),
	@XmlEnumValue("5.5.52") _5_5_52("5.5.52"),
	@XmlEnumValue("5.5.53") _5_5_53("5.5.53"),
	@XmlEnumValue("5.5.54") _5_5_54("5.5.54"),
	@XmlEnumValue("5.5.55") _5_5_55("5.5.55"),
	@XmlEnumValue("5.5.58") _5_5_58("5.5.58"),
	@XmlEnumValue("5.5.59") _5_5_59("5.5.59"),
	@XmlEnumValue("5.5.60") _5_5_60("5.5.60"),
	@XmlEnumValue("5.5.70") _5_5_70("5.5.70"),
	@XmlEnumValue("5.5.85") _5_5_85("5.5.85"),
	@XmlEnumValue("5.5.99") _5_5_99("5.5.99"),
	@XmlEnumValue("5.6") _5_6("5.6"),
	@XmlEnumValue("5.6.05") _5_6_05("5.6.05"),
	@XmlEnumValue("5.6.10") _5_6_10("5.6.10"),
	@XmlEnumValue("5.6.15") _5_6_15("5.6.15"),
	@XmlEnumValue("5.6.20") _5_6_20("5.6.20"),
	@XmlEnumValue("5.6.22") _5_6_22("5.6.22"),
	@XmlEnumValue("5.6.25") _5_6_25("5.6.25"),
	@XmlEnumValue("5.6.30") _5_6_30("5.6.30"),
	@XmlEnumValue("5.6.31") _5_6_31("5.6.31"),
	@XmlEnumValue("5.6.32") _5_6_32("5.6.32"),
	@XmlEnumValue("5.6.42") _5_6_42("5.6.42"),
	@XmlEnumValue("5.6.43") _5_6_43("5.6.43"),
	@XmlEnumValue("5.6.46") _5_6_46("5.6.46"),
	@XmlEnumValue("5.6.47") _5_6_47("5.6.47"),
	@XmlEnumValue("5.6.48") _5_6_48("5.6.48"),
	@XmlEnumValue("5.6.51") _5_6_51("5.6.51"),
	@XmlEnumValue("5.6.52") _5_6_52("5.6.52"),
	@XmlEnumValue("5.6.53") _5_6_53("5.6.53"),
	@XmlEnumValue("5.6.54") _5_6_54("5.6.54"),
	@XmlEnumValue("5.6.55") _5_6_55("5.6.55"),
	@XmlEnumValue("5.6.58") _5_6_58("5.6.58"),
	@XmlEnumValue("5.6.59") _5_6_59("5.6.59"),
	@XmlEnumValue("5.6.60") _5_6_60("5.6.60"),
	@XmlEnumValue("5.6.70") _5_6_70("5.6.70"),
	@XmlEnumValue("5.6.85") _5_6_85("5.6.85"),
	@XmlEnumValue("5.6.99") _5_6_99("5.6.99"),
	@XmlEnumValue("5.7") _5_7("5.7"),
	@XmlEnumValue("5.7.10") _5_7_10("5.7.10"),
	@XmlEnumValue("5.7.20") _5_7_20("5.7.20"),
	@XmlEnumValue("5.7.70") _5_7_70("5.7.70"),
	@XmlEnumValue("5.7.85") _5_7_85("5.7.85"),
	@XmlEnumValue("5.8") _5_8("5.8"),
	@XmlEnumValue("6") _6("6"),
	@XmlEnumValue("6.1") _6_1("6.1"),
	@XmlEnumValue("6.2") _6_2("6.2"),
	@XmlEnumValue("6.2.10") _6_2_10("6.2.10"),
	@XmlEnumValue("6.2.20") _6_2_20("6.2.20"),
	@XmlEnumValue("6.3") _6_3("6.3"),
	@XmlEnumValue("6.3.16") _6_3_16("6.3.16"),
	@XmlEnumValue("6.3.17") _6_3_17("6.3.17"),
	@XmlEnumValue("6.3.18") _6_3_18("6.3.18"),
	@XmlEnumValue("6.4") _6_4("6.4"),
	@XmlEnumValue("6.4.11") _6_4_11("6.4.11"),
	@XmlEnumValue("6.5") _6_5("6.5"),
	@XmlEnumValue("6.5.11") _6_5_11("6.5.11"),
	@XmlEnumValue("6.5.21") _6_5_21("6.5.21"),
	@XmlEnumValue("6.5.32") _6_5_32("6.5.32"),
	@XmlEnumValue("6.5.33") _6_5_33("6.5.33"),
	@XmlEnumValue("6.5.34") _6_5_34("6.5.34"),
	@XmlEnumValue("6.5.35") _6_5_35("6.5.35"),
	@XmlEnumValue("6.5.36") _6_5_36("6.5.36"),
	@XmlEnumValue("6.5.37") _6_5_37("6.5.37"),
	@XmlEnumValue("6.5.38") _6_5_38("6.5.38"),
	@XmlEnumValue("6.5.39") _6_5_39("6.5.39"),
	@XmlEnumValue("6.5.40") _6_5_40("6.5.40"),
	@XmlEnumValue("6.5.41") _6_5_41("6.5.41"),
	@XmlEnumValue("6.5.42") _6_5_42("6.5.42"),
	@XmlEnumValue("6.5.43") _6_5_43("6.5.43"),
	@XmlEnumValue("6.5.44") _6_5_44("6.5.44"),
	@XmlEnumValue("6.5.51") _6_5_51("6.5.51"),
	@XmlEnumValue("6.5.54") _6_5_54("6.5.54"),
	@XmlEnumValue("6.5.58") _6_5_58("6.5.58"),
	@XmlEnumValue("6.5.60") _6_5_60("6.5.60"),
	@XmlEnumValue("6.5.61") _6_5_61("6.5.61"),
	@XmlEnumValue("6.5.62") _6_5_62("6.5.62"),
	@XmlEnumValue("6.5.63") _6_5_63("6.5.63"),
	@XmlEnumValue("6.5.64") _6_5_64("6.5.64"),
	@XmlEnumValue("6.5.65") _6_5_65("6.5.65"),
	@XmlEnumValue("6.5.66") _6_5_66("6.5.66"),
	@XmlEnumValue("6.5.67") _6_5_67("6.5.67"),
	@XmlEnumValue("6.5.68") _6_5_68("6.5.68"),
	@XmlEnumValue("6.5.69") _6_5_69("6.5.69"),
	@XmlEnumValue("6.5.71") _6_5_71("6.5.71"),
	@XmlEnumValue("6.5.72") _6_5_72("6.5.72"),
	@XmlEnumValue("6.5.73") _6_5_73("6.5.73"),
	@XmlEnumValue("6.5.74") _6_5_74("6.5.74"),
	@XmlEnumValue("6.5.75") _6_5_75("6.5.75"),
	@XmlEnumValue("6.5.76") _6_5_76("6.5.76"),
	@XmlEnumValue("6.5.77") _6_5_77("6.5.77"),
	@XmlEnumValue("6.5.78") _6_5_78("6.5.78"),
	@XmlEnumValue("6.5.85") _6_5_85("6.5.85"),
	@XmlEnumValue("6.5.88") _6_5_88("6.5.88"),
	@XmlEnumValue("6.5.89") _6_5_89("6.5.89"),
	@XmlEnumValue("6.5.95") _6_5_95("6.5.95"),
	@XmlEnumValue("6.5.96") _6_5_96("6.5.96"),
	@XmlEnumValue("6.5.97") _6_5_97("6.5.97"),
	@XmlEnumValue("6.5.98") _6_5_98("6.5.98"),
	@XmlEnumValue("6.5.99") _6_5_99("6.5.99"),
	@XmlEnumValue("6.9") _6_9("6.9"),
	@XmlEnumValue("6.9.01") _6_9_01("6.9.01"),
	@XmlEnumValue("7") _7("7"),
	@XmlEnumValue("7.1") _7_1("7.1"),
	@XmlEnumValue("7.1.11") _7_1_11("7.1.11"),
	@XmlEnumValue("7.1.12") _7_1_12("7.1.12"),
	@XmlEnumValue("7.1.13") _7_1_13("7.1.13"),
	@XmlEnumValue("7.1.20") _7_1_20("7.1.20"),
	@XmlEnumValue("7.1.50") _7_1_50("7.1.50"),
	@XmlEnumValue("7.1.60") _7_1_60("7.1.60"),
	@XmlEnumValue("7.1.71") _7_1_71("7.1.71"),
	@XmlEnumValue("7.1.72") _7_1_72("7.1.72"),
	@XmlEnumValue("7.1.79") _7_1_79("7.1.79"),
	@XmlEnumValue("7.1.90") _7_1_90("7.1.90"),
	@XmlEnumValue("7.2") _7_2("7.2"),
	@XmlEnumValue("7.2.10") _7_2_10("7.2.10"),
	@XmlEnumValue("7.2.20") _7_2_20("7.2.20"),
	@XmlEnumValue("7.2.25") _7_2_25("7.2.25"),
	@XmlEnumValue("7.2.29") _7_2_29("7.2.29"),
	@XmlEnumValue("7.2.30") _7_2_30("7.2.30"),
	@XmlEnumValue("7.3") _7_3("7.3"),
	@XmlEnumValue("7.3.12") _7_3_12("7.3.12"),
	@XmlEnumValue("7.3.13") _7_3_13("7.3.13"),
	@XmlEnumValue("7.3.14") _7_3_14("7.3.14"),
	@XmlEnumValue("7.3.21") _7_3_21("7.3.21"),
	@XmlEnumValue("7.3.22") _7_3_22("7.3.22"),
	@XmlEnumValue("7.3.23") _7_3_23("7.3.23"),
	@XmlEnumValue("7.3.31") _7_3_31("7.3.31"),
	@XmlEnumValue("7.3.40") _7_3_40("7.3.40"),
	@XmlEnumValue("7.3.41") _7_3_41("7.3.41"),
	@XmlEnumValue("7.3.42") _7_3_42("7.3.42"),
	@XmlEnumValue("7.3.43") _7_3_43("7.3.43"),
	@XmlEnumValue("7.3.44") _7_3_44("7.3.44"),
	@XmlEnumValue("7.3.45") _7_3_45("7.3.45"),
	@XmlEnumValue("7.3.46") _7_3_46("7.3.46"),
	@XmlEnumValue("7.3.47") _7_3_47("7.3.47"),
	@XmlEnumValue("7.3.48") _7_3_48("7.3.48"),
	@XmlEnumValue("7.3.49") _7_3_49("7.3.49"),
	@XmlEnumValue("7.3.51") _7_3_51("7.3.51"),
	@XmlEnumValue("7.3.52") _7_3_52("7.3.52"),
	@XmlEnumValue("7.3.53") _7_3_53("7.3.53"),
	@XmlEnumValue("7.3.54") _7_3_54("7.3.54"),
	@XmlEnumValue("7.3.55") _7_3_55("7.3.55"),
	@XmlEnumValue("7.3.56") _7_3_56("7.3.56"),
	@XmlEnumValue("7.3.61") _7_3_61("7.3.61"),
	@XmlEnumValue("7.3.62") _7_3_62("7.3.62"),
	@XmlEnumValue("7.3.63") _7_3_63("7.3.63"),
	@XmlEnumValue("7.3.64") _7_3_64("7.3.64"),
	@XmlEnumValue("7.3.65") _7_3_65("7.3.65"),
	@XmlEnumValue("7.3.66") _7_3_66("7.3.66"),
	@XmlEnumValue("7.3.71") _7_3_71("7.3.71"),
	@XmlEnumValue("7.3.72") _7_3_72("7.3.72"),
	@XmlEnumValue("7.3.73") _7_3_73("7.3.73"),
	@XmlEnumValue("7.3.78") _7_3_78("7.3.78"),
	@XmlEnumValue("7.3.79") _7_3_79("7.3.79"),
	@XmlEnumValue("7.3.81") _7_3_81("7.3.81"),
	@XmlEnumValue("7.3.82") _7_3_82("7.3.82"),
	@XmlEnumValue("7.3.83") _7_3_83("7.3.83"),
	@XmlEnumValue("7.3.84") _7_3_84("7.3.84"),
	@XmlEnumValue("7.3.85") _7_3_85("7.3.85"),
	@XmlEnumValue("7.3.89") _7_3_89("7.3.89"),
	@XmlEnumValue("7.4") _7_4("7.4"),
	@XmlEnumValue("7.4.10") _7_4_10("7.4.10"),
	@XmlEnumValue("7.4.30") _7_4_30("7.4.30"),
	@XmlEnumValue("7.4.50") _7_4_50("7.4.50"),
	@XmlEnumValue("7.4.70") _7_4_70("7.4.70"),
	@XmlEnumValue("7.4.90") _7_4_90("7.4.90"),
	@XmlEnumValue("8") _8("8"),
	@XmlEnumValue("8.1") _8_1("8.1"),
	@XmlEnumValue("8.1.10") _8_1_10("8.1.10"),
	@XmlEnumValue("8.1.20") _8_1_20("8.1.20"),
	@XmlEnumValue("8.1.30") _8_1_30("8.1.30"),
	@XmlEnumValue("8.1.40") _8_1_40("8.1.40"),
	@XmlEnumValue("8.1.50") _8_1_50("8.1.50"),
	@XmlEnumValue("8.1.60") _8_1_60("8.1.60"),
	@XmlEnumValue("8.1.70") _8_1_70("8.1.70"),
	@XmlEnumValue("8.1.90") _8_1_90("8.1.90"),
	@XmlEnumValue("8.2") _8_2("8.2"),
	@XmlEnumValue("8.2.10") _8_2_10("8.2.10"),
	@XmlEnumValue("8.2.50") _8_2_50("8.2.50"),
	@XmlEnumValue("8.2.90") _8_2_90("8.2.90"),
	@XmlEnumValue("8.3") _8_3("8.3"),
	@XmlEnumValue("8.3.10") _8_3_10("8.3.10"),
	@XmlEnumValue("8.3.11") _8_3_11("8.3.11"),
	@XmlEnumValue("8.4") _8_4("8.4"),
	@XmlEnumValue("8.4.10") _8_4_10("8.4.10"),
	@XmlEnumValue("8.4.20") _8_4_20("8.4.20"),
	@XmlEnumValue("8.4.50") _8_4_50("8.4.50"),
	@XmlEnumValue("8.4.70") _8_4_70("8.4.70"),
	@XmlEnumValue("8.4.90") _8_4_90("8.4.90"),
	@XmlEnumValue("8.5") _8_5("8.5"),
	@XmlEnumValue("8.5.10") _8_5_10("8.5.10"),
	@XmlEnumValue("8.5.20") _8_5_20("8.5.20"),
	@XmlEnumValue("9") _9("9"),
	@XmlEnumValue("9.1") _9_1("9.1"),
	@XmlEnumValue("9.1.10") _9_1_10("9.1.10"),
	@XmlEnumValue("9.1.50") _9_1_50("9.1.50"),
	@XmlEnumValue("9.2") _9_2("9.2"),
	@XmlEnumValue("9.2.10") _9_2_10("9.2.10"),
	@XmlEnumValue("9.2.20") _9_2_20("9.2.20"),
	@XmlEnumValue("9.2.21") _9_2_21("9.2.21"),
	@XmlEnumValue("9.2.22") _9_2_22("9.2.22"),
	@XmlEnumValue("9.2.23") _9_2_23("9.2.23"),
	@XmlEnumValue("9.2.24") _9_2_24("9.2.24"),
	@XmlEnumValue("9.2.30") _9_2_30("9.2.30"),
	@XmlEnumValue("9.2.40") _9_2_40("9.2.40"),
	@XmlEnumValue("9.2.60") _9_2_60("9.2.60"),
	@XmlEnumValue("9.3") _9_3("9.3"),
	@XmlEnumValue("9.9") _9_9("9.9"),
	@XmlEnumValue("9.9.70") _9_9_70("9.9.70");

	private final String id;

	private OrganismINSEECat(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.id;
	}

	/**
	 * Get OrganismINSEECat given formeJuridique
	 * @param formeJuridique forme juridique
	 * @return
	 */
	public static OrganismINSEECat fromId(String formeJuridique) {
		return Arrays.stream(OrganismINSEECat.values()).filter(organismNafCode -> organismNafCode.getId().equals(formeJuridique))
				.findFirst().orElse(null);
	}
}
