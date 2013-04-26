package com.stratosim.server;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceLibrary;
import com.stratosim.shared.devicemodel.DeviceLibraryBuilder;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.devicemodel.DeviceTypeBuilder;
import com.stratosim.shared.validator.ParameterValidator;
import com.stratosim.shared.validator.ParameterValueValidator;
import com.stratosim.shared.validator.RegExValidator;

public enum DeviceManagerInstance implements DeviceManager {

  INSTANCE;

  private DeviceLibrary defaultLibrary;

  DeviceManagerInstance() {
    DeviceLibraryBuilder defaultLibraryBuilder = new DeviceLibraryBuilder();

    // Validation Regex for Parameters
    String DECIMAL = "[0-9]+(?:\\.[0-9]+)?";
    String NUMERIC_VALUE_BASE = DECIMAL + "(?:e-?" + DECIMAL + ")?(?:m|u|n|p|f|k|meg|g|t)?";
    String NEGATIVE_PREFIX = "-?";

    ParameterValidator resistanceValidator =
        new ParameterValueValidator(new RegExValidator("^" + NUMERIC_VALUE_BASE + "ohms" + "$")); // TODO(tpondich):
                                                                                                  // Omega
    ParameterValidator capacitanceValidator =
        new ParameterValueValidator(new RegExValidator("^" + NUMERIC_VALUE_BASE + "F" + "$"));
    ParameterValidator inductanceValidator =
        new ParameterValueValidator(new RegExValidator("^" + NUMERIC_VALUE_BASE + "H" + "$"));
    ParameterValidator frequencyValidator =
        new ParameterValueValidator(new RegExValidator("^" + NUMERIC_VALUE_BASE + "Hz" + "$"));
    ParameterValidator timeValidator =
        new ParameterValueValidator(new RegExValidator("^" + NUMERIC_VALUE_BASE + "s" + "$"));
    ParameterValidator voltageValidator =
        new ParameterValueValidator(new RegExValidator("^" + NEGATIVE_PREFIX + NUMERIC_VALUE_BASE
            + "V" + "$"));
    ParameterValidator currentValidator =
        new ParameterValueValidator(new RegExValidator("^" + NEGATIVE_PREFIX + NUMERIC_VALUE_BASE
            + "A" + "$"));
    ParameterValidator fractionValidator =
        new ParameterValueValidator(new RegExValidator("^" + DECIMAL + "(?:e-?" + DECIMAL + ")?"
            + "$"));
    ParameterValidator radiansValidator =
        new ParameterValueValidator(new RegExValidator("^" + DECIMAL + "(?:e-?" + DECIMAL + ")?"
            + "rad" + "$"));
    ParameterValidator textValidator =
        new ParameterValueValidator(new RegExValidator("^([a-zA-Z0-9_-]|\\s)+$"));
    ParameterValidator colorValidator =
        new ParameterValueValidator(new RegExValidator("^(red|orange|yellow|green|blue|purple)$"));

    // Linear Components
    DeviceTypeBuilder resistorBuilder =
        new DeviceTypeBuilder()
            .type("Resistor")
            .prefix("R")
            .port("0", 60, 0)
            .port("1", -60, 0)
            .parameter("Value", resistanceValidator)
            .spiceTemplate("R{%SPICENAME} {%0} {%1} {%Value}")
            .labelTemplate("{%Name}\n{%Value}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 30 6 array astore concat 0 15 moveto 30 15 lineto 35 5 lineto 45 25 lineto 55 5 lineto 65 25 lineto 75 5 lineto 85 25 lineto 90 15 lineto 120  15 lineto stroke restore")
            .height(30)
            .width(120);
    // Value suggestions taken from:
    // http://www.electro-tech-online.com/general-electronics-chat/35528-most-common-resistors-keep-stock.html
    // TODO(tpondich): Should we loop to create these or do this during lib generation?

    DeviceType customResistor = resistorBuilder.build();
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("10ohms")
        .parameterdefault("Value", "10ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("20ohms")
        .parameterdefault("Value", "20ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("30ohms")
        .parameterdefault("Value", "30ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("40ohms")
        .parameterdefault("Value", "40ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("50ohms")
        .parameterdefault("Value", "50ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("60ohms")
        .parameterdefault("Value", "60ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("70ohms")
        .parameterdefault("Value", "70ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("80ohms")
        .parameterdefault("Value", "80ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("90ohms")
        .parameterdefault("Value", "90ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("100ohms")
        .parameterdefault("Value", "100ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("200ohms")
        .parameterdefault("Value", "200ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("300ohms")
        .parameterdefault("Value", "300ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("400ohms")
        .parameterdefault("Value", "400ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("500ohms")
        .parameterdefault("Value", "500ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("600ohms")
        .parameterdefault("Value", "600ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("700ohms")
        .parameterdefault("Value", "700ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("800ohms")
        .parameterdefault("Value", "800ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("900ohms")
        .parameterdefault("Value", "900ohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("1kohms")
        .parameterdefault("Value", "1kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("2kohms")
        .parameterdefault("Value", "2kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("3kohms")
        .parameterdefault("Value", "3kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("4kohms")
        .parameterdefault("Value", "4kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("5kohms")
        .parameterdefault("Value", "5kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("6kohms")
        .parameterdefault("Value", "6kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("7kohms")
        .parameterdefault("Value", "7kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("8kohms")
        .parameterdefault("Value", "8kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("9kohms")
        .parameterdefault("Value", "9kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("10kohms")
        .parameterdefault("Value", "10kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("100kohms")
        .parameterdefault("Value", "100kohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("1Mohms")
        .parameterdefault("Value", "1megohms").build());
    defaultLibraryBuilder.addDeviceType(resistorBuilder.model("10Mohms")
        .parameterdefault("Value", "10megohms").build());

    // These only differ by the symbol. Components should be able to have multiple easily swappable
    // symbols?
    DeviceTypeBuilder capacitorBuilder =
        new DeviceTypeBuilder()
            .type("Capacitor")
            .prefix("C")
            .port("0", 60, 0)
            .port("1", -60, 0)
            .parameter("Value", capacitanceValidator)
            .parameter("IC", voltageValidator)
            .spiceTemplate("C{%SPICENAME} {%0} {%1} {%Value} IC={%IC}")
            .labelTemplate("{%Name}\n{%Value}\nIC={%IC}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 30 6 array astore concat 55 0 moveto 55 30 lineto stroke restore save 1 0 0 -1 0 30 6 array astore concat 65 30 moveto 65 0 lineto stroke restore save 1 0 0 -1 0 30 6 array astore concat 55 15 moveto 0 15 lineto stroke restore save 1 0 0 -1 0 30 6 array astore concat 65 15 moveto 120 15 lineto stroke restore")
            .width(120)
            .height(30);

    // Value suggestions taken from:
    // http://www.rfcafe.com/references/electrical/capacitor-values.htm

    DeviceType customCapacitor = capacitorBuilder.build();
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("1pF")
        .parameterdefault("Value", "1pF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("10pF")
        .parameterdefault("Value", "10pF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("100pF")
        .parameterdefault("Value", "100pF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("1uF")
        .parameterdefault("Value", "1uF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("10uF")
        .parameterdefault("Value", "10uF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("100uF")
        .parameterdefault("Value", "100uF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("1mF")
        .parameterdefault("Value", "1mF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("10mF")
        .parameterdefault("Value", "10mF").parameterdefault("IC", "0V").build());
    defaultLibraryBuilder.addDeviceType(capacitorBuilder.model("100mF")
        .parameterdefault("Value", "100mF").parameterdefault("IC", "0V").build());

    DeviceTypeBuilder inductorBuilder =
        new DeviceTypeBuilder()
            .type("Inductor")
            .prefix("L")
            .port("0", 60, 0)
            .port("1", -60, 0)
            .parameter("Value", inductanceValidator)
            .parameter("IC", currentValidator)
            .spiceTemplate("L{%SPICENAME} {%0} {%1} {%Value} IC={%IC}")
            .labelTemplate("{%Name}\n{%Value}\nIC={%IC}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 60 6 array astore concat 39.988 0 moveto 42.75 -0.02 44.992 6.684 45 14.965 curveto 45 15.008 lineto stroke restore  save 1 0 0 -1 0 60 6 array astore concat 45 15 moveto 45 23.285 40.523 30 35 30 curveto stroke restore save -1 0 0 -1 0 60 6 array astore concat -40.012 0 moveto -37.25 -0.02 -35.008 6.684 -35 14.965 curveto -35 15.008 lineto stroke restore save -1 0 0 -0.666667 0 60 6 array astore concat -35 22.5 moveto -35 34.928 -41.715 45 -50 45 curveto stroke restore save 1 0 0 -1 0 60 6 array astore concat 59.977 0 moveto 62.738 -0.02 64.984 6.684 64.988 14.965 curveto 64.988 15.008 lineto stroke restore save 1 0 0 -0.667168 0 60 6 array astore concat 64.988 22.483 moveto 64.988 34.902 58.277 44.966 50 44.966 curveto stroke restore save -1 0 0 -1 0 60 6 array astore concat -60 0 moveto -57.238 -0.02 -54.996 6.684 -54.988 14.965 curveto -54.988 15.008 lineto stroke restore save -1 0 0 -0.666166 0 60 6 array astore concat -54.988 22.517 moveto -54.988 34.954 -61.711 45.034 -70 45.034 curveto stroke restore  save 1 0 0 -1 0 60 6 array astore concat 79.988 -0.008 moveto 82.75 -0.023 84.992 6.676 85 14.961 curveto 85 15 lineto stroke restore save 1 0 0 -0.666667 0 60 6 array astore concat 85 22.488 moveto 85 34.916 78.285 44.988 70 44.988 curveto stroke restore save -1 0 0 -1 0 60 6 array astore concat -80.012 -0.008 moveto -77.25 -0.023 -75.008 6.676 -75 14.961 curveto -75 15 lineto stroke restore save -1 0 0 -1 0 60 6 array astore concat -75 14.992 moveto -75 23.277 -79.477 29.992 -85 29.992 curveto stroke restore save 1 0 0 -1 0 60 6 array astore concat 35 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 60 6 array astore concat 85 30 moveto 120 30 lineto stroke restore")
            .width(120)
            .height(60);
    DeviceType customInductor = inductorBuilder.build();
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("1pH")
        .parameterdefault("Value", "1pH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("10pH")
        .parameterdefault("Value", "10pH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("100pH")
        .parameterdefault("Value", "100pH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("1uH")
        .parameterdefault("Value", "1uH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("10uH")
        .parameterdefault("Value", "10uH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("100uH")
        .parameterdefault("Value", "100uH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("1mH")
        .parameterdefault("Value", "1mH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("10mH")
        .parameterdefault("Value", "10mH").parameterdefault("IC", "0A").build());
    defaultLibraryBuilder.addDeviceType(inductorBuilder.model("100mH")
        .parameterdefault("Value", "100mH").parameterdefault("IC", "0A").build());

    // Sources
    DeviceTypeBuilder dcVoltageSourceBuilder =
        new DeviceTypeBuilder()
            .type("DC Voltage")
            .prefix("V")
            .port("1", 60, 0)
            .port("0", -60, 0)
            .parameter("Value", voltageValidator)
            .spiceTemplate("V{%SPICENAME} {%0} {%1} {%Value}")
            .labelTemplate("{%Name}\n{%Value}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 50 50 moveto 60 70 lineto 70 50 lineto stroke restore  save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 5 45 moveto 5 35 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 0 40 moveto 10 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 40 moveto 110 40 lineto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110  87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customDCVoltageSource = dcVoltageSourceBuilder.build();
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("1V")
        .parameterdefault("Value", "1V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("1.2V")
        .parameterdefault("Value", "1.2V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("1.5V")
        .parameterdefault("Value", "1.5V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("2.4V")
        .parameterdefault("Value", "2.4V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("3V")
        .parameterdefault("Value", "3V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("5V")
        .parameterdefault("Value", "5V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("9V")
        .parameterdefault("Value", "9V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("10V")
        .parameterdefault("Value", "10V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("12V")
        .parameterdefault("Value", "12V").build());
    defaultLibraryBuilder.addDeviceType(dcVoltageSourceBuilder.model("24V")
        .parameterdefault("Value", "24V").build());

    DeviceTypeBuilder acVoltageSourceBuilder =
        new DeviceTypeBuilder()
            .type("AC Voltage")
            .prefix("V")
            .port("1", 60, 0)
            .port("0", -60, 0)
            .parameter("Offset", voltageValidator)
            .parameter("Peak", voltageValidator)
            .parameter("Freq", frequencyValidator)
            .parameter("Delay", timeValidator)
            .parameter("Damp", fractionValidator)
            .parameter("Phase", radiansValidator)
            .spiceTemplate(
                "V{%SPICENAME} {%0} {%1} AC 1 " + "SIN({%Offset} " + "{%Peak} " + "{%Freq} "
                    + "{%Delay} " + "{%Damp} " + "{%Phase})")
            .labelTemplate("{%Name}\n{%Offset} + {%Peak} @ {%Freq}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 40 60 moveto 40 45 50 45 50 45 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 60 moveto 60 45 50 45 50 45 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 60 moveto 60 75 70 75 70 75 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 80 60 moveto 80 75 70 75 70 75 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 5 45 moveto 5 35 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 0 40 moveto 10 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 40 moveto 110 40 lineto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110 87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customACVoltageSource = acVoltageSourceBuilder.build();
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("1V @ 1kHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "1V")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("10V @ 1kHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "5V")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("50V @ 1kHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "50V")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("100V @ 1kHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "100V")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("1V @ 1MHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "1V")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("10V @ 1MHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "5V")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("50V @ 1MHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "50V")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acVoltageSourceBuilder.model("100V @ 1MHz")
        .parameterdefault("Offset", "0V").parameterdefault("Peak", "100V")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());

    DeviceTypeBuilder dcCurrentSourceBuilder =
        new DeviceTypeBuilder()
            .type("DC Current")
            .prefix("I")
            .port("0", 60, 0)
            .port("1", -60, 0)
            .parameter("Value", currentValidator)
            .spiceTemplate("I{%SPICENAME} {%0} {%1} DC {%Value}")
            .labelTemplate("{%Name}\n{%Value}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 40 60 moveto 80 60 lineto stroke restore 80 60 moveto 70 65 lineto fill save 1 0 0 -1 0 120 6 array astore concat 80 60 moveto 70 55 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 70 65 moveto 80 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 5 45 moveto 5 35 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 0 40 moveto 10 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 40 moveto 110 40 lineto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110 87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customDCCurrentSource = dcCurrentSourceBuilder.build();
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("1pA")
        .parameterdefault("Value", "1pA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("10pA")
        .parameterdefault("Value", "10pA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("100pA")
        .parameterdefault("Value", "100pA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("1uA")
        .parameterdefault("Value", "1uA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("10uA")
        .parameterdefault("Value", "10uA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("100uA")
        .parameterdefault("Value", "100uA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("1mA")
        .parameterdefault("Value", "1mA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("10mA")
        .parameterdefault("Value", "10mA").build());
    defaultLibraryBuilder.addDeviceType(dcCurrentSourceBuilder.model("100mA")
        .parameterdefault("Value", "100mA").build());

    // This has the same symbol as AC Voltage Source ?... fail.
    DeviceTypeBuilder acCurrentSourceBuilder =
        new DeviceTypeBuilder()
            .type("AC Current")
            .prefix("I")
            .port("1", 60, 0)
            .port("0", -60, 0)
            .parameter("Offset", currentValidator)
            .parameter("Peak", currentValidator)
            .parameter("Freq", frequencyValidator)
            .parameter("Delay", timeValidator)
            .parameter("Damp", fractionValidator)
            .parameter("Phase", radiansValidator)
            .spiceTemplate(
                "I{%SPICENAME} {%0} {%1} AC 1 " + "SIN({%Offset} " + "{%Peak} " + "{%Freq} "
                    + "{%Delay} " + "{%Damp} " + "{%Phase})")
            .labelTemplate("{%Name}\n{%Offset} + {%Peak} @ {%Freq}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 5 45 moveto 5 35 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 0 40 moveto 10 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 40 moveto 110 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 45 60 moveto 75 60 lineto stroke restore 75 60 moveto 65 65 lineto fill save 1 0 0 -1 0 120 6 array astore concat 75 60 moveto 65 55 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 65 65 moveto 75 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 40 60 moveto 40 45 50 45 50 45 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 60 moveto 60 45 50 45 50 45 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 60 moveto 60 75 70 75 70 75 curveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 80 60 moveto 80 75 70 75 70 75 curveto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110 87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customACCurrentSource = acCurrentSourceBuilder.build();
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("1mA @ 1kHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "1mA")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("10mA @ 1kHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "10mA")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("50mA @ 1kHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "50mA")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("100mA @ 1kHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "100mA")
        .parameterdefault("Freq", "1kHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("1mA @ 1MHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "1mA")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("10mA @ 1MHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "10mA")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("50mA @ 1MHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "50mA")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());
    defaultLibraryBuilder.addDeviceType(acCurrentSourceBuilder.model("100mA @ 1MHz")
        .parameterdefault("Offset", "0A").parameterdefault("Peak", "100mA")
        .parameterdefault("Freq", "1megHz").parameterdefault("Delay", "0s")
        .parameterdefault("Damp", "1.0").parameterdefault("Phase", "0rad").build());

    // Non Linear Devices
    DeviceTypeBuilder diodeBuilder =
        new DeviceTypeBuilder()
            .type("Diode")
            .prefix("D")
            .port("A", -40, 0)
            .port("K", 40, 0)
            .parameter("Saturation Current", currentValidator)
            .parameter("Ohmic Resistance", resistanceValidator)
            .parameter("Emission Coefficient", fractionValidator)
            .parameter("Breakdown Voltage", voltageValidator)
            .parameter("Breakdown Current", currentValidator)
            .parameter("Junction Capacitance", capacitanceValidator)
            .parameter("Grading Coefficient", fractionValidator)
            .parameter("Transit Time", timeValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DIODE} D(IS={%Saturation Current} " + "RS={%Ohmic Resistance} "
                    + "BV={%Breakdown Voltage} " + "IBV={%Breakdown Current} "
                    + "CJO={%Junction Capacitance} " + "M={%Grading Coefficient} "
                    + "N={%Emission Coefficient} " + "TT={%Transit Time})\n"
                    + "D{%SPICENAME} {%A} {%K} {%MODELNAME.DIODE}")
            .labelTemplate("{%Name}\n{%MODEL}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 40 6 array astore concat 20 0 moveto 20 40 lineto 60 20 lineto closepath 20 0 moveto stroke restore save 1 0 0 -1 0 40 6 array astore concat 60 0 moveto 60 40 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 60 20 moveto 80 20 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 0 20 moveto 20 20 lineto stroke restore")
            .width(80)
            .height(40);
    // There are actually a couple more parameters, but don't seem to be used in basic models
    // It is weird that VJ is missing though and it is also weird EG is missing.
    DeviceType customDiode = diodeBuilder.build();
    defaultLibraryBuilder.addDeviceType(diodeBuilder
        .model("Ideal")
        .parameterdefault("Saturation Current", "1fA")
        .parameterdefault("Ohmic Resistance", "0ohms")
        .parameterdefault("Breakdown Voltage", "1tV")
        // TODO(tpondich): Investigate infinity
        .parameterdefault("Breakdown Current", "1fA")
        .parameterdefault("Junction Capacitance", "0F")
        .parameterdefault("Grading Coefficient", "0.5")
        .parameterdefault("Emission Coefficient", "1").parameterdefault("Transit Time", "0s")
        .build());
    // This is the Zetex Rectifier Library from diodes.com. We have permission in email to use any
    // of these models.
    // TODO(tpondich): Attribution
     defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A01")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "50.0V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "277pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A02")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "100V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "277pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A03")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "200V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "277pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A04")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "400V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "277pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A05")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "600V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "148pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A06")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "800V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "148pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
  defaultLibraryBuilder.addDeviceType(diodeBuilder.model("10A07")
        .parameterdefault("Saturation Current", "844nA")
        .parameterdefault("Ohmic Resistance", "2.06mohms")
        .parameterdefault("Breakdown Voltage", "1.00kV")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "148pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "2.06")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4001")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "50.0V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "39.8pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4001G")
        .parameterdefault("Saturation Current", "65.4pA")
        .parameterdefault("Ohmic Resistance", "42.2mohms")
        .parameterdefault("Breakdown Voltage", "50.0V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "14.8pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.36")
        .parameterdefault("Transit Time", "2.88us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4002")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "100V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "39.8pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4003")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "200V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "39.8pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4004")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "400V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "39.8pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4005")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "600V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "26.5pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4006")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "800V")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "26.5pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N4007")
        .parameterdefault("Saturation Current", "76.9pA")
        .parameterdefault("Ohmic Resistance", "42.0mohms")
        .parameterdefault("Breakdown Voltage", "1.00kV")
        .parameterdefault("Breakdown Current", "5.00uA")
        .parameterdefault("Junction Capacitance", "26.5pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.45")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5400")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "50.0V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "125pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5401")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "100V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "125pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5402")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "200V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "125pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5404")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "400V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "125pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5406")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "600V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "53.0pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5407")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "800V")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "53.0pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());
    defaultLibraryBuilder.addDeviceType(diodeBuilder.model("1N5408")
        .parameterdefault("Saturation Current", "63.0nA")
        .parameterdefault("Ohmic Resistance", "14.1mohms")
        .parameterdefault("Breakdown Voltage", "1.00kV")
        .parameterdefault("Breakdown Current", "10.0uA")
        .parameterdefault("Junction Capacitance", "53.0pF")
        .parameterdefault("Grading Coefficient", "0.333")
        .parameterdefault("Emission Coefficient", "1.70")
        .parameterdefault("Transit Time", "4.32us").build());

    DeviceTypeBuilder npnTransistorBuilder =
        new DeviceTypeBuilder()
            .type("NPN")
            .prefix("Q")
            .port("C", -60, 0)
            .port("B", 0, 60)
            .port("E", 60, 0)
            .parameter("Forward Gain", fractionValidator)
            .parameter("Reverse Gain", fractionValidator)
            .parameter("Saturation Current", currentValidator)
            .parameter("B-E Capacitance", capacitanceValidator)
            .parameter("B-C Capacitance", capacitanceValidator)
            .parameter("B-E Potential", voltageValidator)
            .parameter("B-C Potential", voltageValidator)
            .parameter("Forward Early Voltage", voltageValidator)
            .parameter("Reverse Early Voltage", voltageValidator)
            .parameter("Forward Ideality Factor", fractionValidator)
            .parameter("Reverse Ideality Factor", fractionValidator)
            // There are actually a lot more parameters, but these are main ones.
            .spiceTemplate(
                ".model {%MODELNAME.BJT} NPN (BF={%Forward Gain} " + "BR={%Reverse Gain} "
                    + "IS={%Saturation Current} " + "CJE={%B-E Capacitance} "
                    + "CJC={%B-C Capacitance} " + "VJE={%B-E Potential} " + "VJC={%B-C Potential} "
                    + "VAF={%Forward Early Voltage} " + "VAR={%Reverse Early Voltage} "
                    + "NF={%Forward Ideality Factor} " + "NR={%Reverse Ideality Factor})\n"
                    + "Q{%SPICENAME} {%C} {%B} {%E} {%MODELNAME.BJT}")
            .labelTemplate("{%Name}\n{%MODEL}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 60 100 moveto 60 120 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 90 100 moveto 30 100 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 40 100 moveto 10 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 80 100 moveto 110 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 20.258 64.445 moveto 10 60 lineto 12.598 70.875 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110 87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customNPNTransistor = npnTransistorBuilder.build();
    defaultLibraryBuilder.addDeviceType(npnTransistorBuilder.model("Ideal")
        .parameterdefault("Forward Gain", "100").parameterdefault("Reverse Gain", "1")
        .parameterdefault("Saturation Current", "1e-16A").parameterdefault("B-E Capacitance", "0F")
        .parameterdefault("B-C Capacitance", "0F").parameterdefault("B-E Potential", "0.75V")
        .parameterdefault("B-C Potential", "0.75V")
        .parameterdefault("Forward Early Voltage", "1tV")
        .parameterdefault("Reverse Early Voltage", "1tV")
        .parameterdefault("Forward Ideality Factor", "1")
        .parameterdefault("Reverse Ideality Factor", "1").build());

    DeviceTypeBuilder pnpTransistorBuilder =
        new DeviceTypeBuilder()
            .type("PNP")
            .prefix("Q")
            .port("C", -60, 0)
            .port("B", 0, 60)
            .port("E", 60, 0)
            .parameter("Forward Gain", fractionValidator)
            .parameter("Reverse Gain", fractionValidator)
            .parameter("Saturation Current", currentValidator)
            .parameter("B-E Capacitance", capacitanceValidator)
            .parameter("B-C Capacitance", capacitanceValidator)
            .parameter("B-E Potential", voltageValidator)
            .parameter("B-C Potential", voltageValidator)
            .parameter("Forward Early Voltage", voltageValidator)
            .parameter("Reverse Early Voltage", voltageValidator)
            .parameter("Forward Ideality Factor", fractionValidator)
            .parameter("Reverse Ideality Factor", fractionValidator)
            // There are actually a lot more parameters, but these are main ones.
            // They were selected based on: http://ecee.colorado.edu/~bart/book/book/contents.htm.
            .spiceTemplate(
                ".model {%MODELNAME.BJT} PNP (BF={%Forward Gain} " + "BR={%Reverse Gain} "
                    + "IS={%Saturation Current} " + "CJE={%B-E Capacitance} "
                    + "CJC={%B-C Capacitance} " + "VJE={%B-E Potential} " + "VJC={%B-C Potential} "
                    + "VAF={%Forward Early Voltage} " + "VAR={%Reverse Early Voltage} "
                    + "NF={%Forward Ideality Factor} " + "NR={%Reverse Ideality Factor} "
                    + "Q{%SPICENAME} {%C} {%B} {%E} {%MODELNAME.BJT})\n")
            .labelTemplate("{%Name}\n{%MODEL}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 60 100 moveto 60 120 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 90 100 moveto 30 100 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 40 100 moveto 10 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 80 100 moveto 110 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 90.258 95.555 moveto 80 100 lineto 82.598 89.125 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save -0.984301 0 0 -1 0 120 6 array astore concat -10.957 60 moveto -10.957 87.613 -33.344 110 -60.957 110 curveto -88.57 110 -110.957 87.613 -110.957 60 curveto -110.957 32.387 -88.57 10 -60.957 10 curveto -33.344 10 -10.957 32.387 -10.957 60 curveto closepath -10.957 60 moveto stroke restore")
            .width(120)
            .height(120);
    DeviceType customPNPTransistor = pnpTransistorBuilder.build();
    defaultLibraryBuilder.addDeviceType(pnpTransistorBuilder.model("Ideal")
        .parameterdefault("Forward Gain", "100").parameterdefault("Reverse Gain", "1")
        .parameterdefault("Saturation Current", "1e-16A").parameterdefault("B-E Capacitance", "0F")
        .parameterdefault("B-C Capacitance", "0F").parameterdefault("B-E Potential", "0.75V")
        .parameterdefault("B-C Potential", "0.75V")
        .parameterdefault("Forward Early Voltage", "1tV")
        .parameterdefault("Reverse Early Voltage", "1tV")
        .parameterdefault("Forward Ideality Factor", "1")
        .parameterdefault("Reverse Ideality Factor", "1").build());

    DeviceTypeBuilder nChannelMosfetBuilder =
        new DeviceTypeBuilder()
            .type("N Channel")
            .prefix("Q")
            .port("D", -60, 0)
            .port("G", 0, 60)
            .port("S", 60, 0)
            // There are actually a lot more parameters. I don't know what's really useful.
            .parameter("Threshold Voltage", voltageValidator)
            .parameter("Transconductance", fractionValidator)
            // Should be A/V^2

            .spiceTemplate(
                ".model {%MODELNAME.FET} NMOS (VTO={%Threshold Voltage} "
                    + "KP={%Transconductance}" + ")\n"
                    + "M{%SPICENAME} {%D} {%G} {%S} {%S} {%MODELNAME.FET}")
            .labelTemplate("{%Name}\n{%MODEL}")
            .draw("0 0 1 setrgbcolor save 0 1 -1 0 0 120 6 array astore concat -10 -60 moveto -10 -32.387 -32.387 -10 -60 -10 curveto -87.613 -10 -110 -32.387  -110 -60 curveto -110 -87.613 -87.613 -110 -60 -110 curveto -32.387 -110 -10 -87.613 -10 -60 curveto closepath -10 -60 moveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 100 moveto 60 120 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 90 100 moveto 30 100 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 60 moveto 90 60 lineto 90 90 lineto 30 90 lineto 30 60 lineto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 26.25 67.5 moveto 30 60 lineto 33.75 67.5 lineto stroke restore")
            .width(120)
            .height(120);
    DeviceType customNChannelMosfet = nChannelMosfetBuilder.build();
    defaultLibraryBuilder.addDeviceType(nChannelMosfetBuilder.model("Ideal")
        .parameterdefault("Threshold Voltage", "1V").parameterdefault("Transconductance", "1e-5")
        .build());

    DeviceTypeBuilder pChannelMosfetBuilder =
        new DeviceTypeBuilder()
            .type("P Channel")
            .prefix("Q")
            .port("D", -60, 0)
            .port("G", 0, 60)
            .port("S", 60, 0)
            // There are actually a lot more parameters. I don't know what's really useful.
            .parameter("Threshold Voltage", voltageValidator)
            .parameter("Transconductance", fractionValidator)
            // Should be A/V^2

            .spiceTemplate(
                ".model {%MODELNAME.FET} PMOS (VTO={%Threshold Voltage} "
                    + "KP={%Transconductance}" + ")\n"
                    + "M{%SPICENAME} {%D} {%G} {%S} {%S} {%MODELNAME.FET}")
            .labelTemplate("{%Name}\n{%MODEL}")
            .draw("0 0 1 setrgbcolor save 0 1 -1 0 0 120 6 array astore concat -10 -60 moveto -10 -32.387 -32.387 -10 -60 -10 curveto -87.613 -10 -110 -32.387  -110 -60 curveto -110 -87.613 -87.613 -110 -60 -110 curveto -32.387 -110 -10  -87.613 -10 -60 curveto closepath -10 -60 moveto stroke restore save 1 0 0 -1 0 120 6 array astore concat 60 100 moveto 60 120 lineto stroke restore 0.8 w save 1 0 0 -1 0 120 6 array astore concat 90 100 moveto 30 100 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 60 moveto 90 60 lineto 90 90 lineto 30 90 lineto 30 60 lineto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 86.25 82.5 moveto 90 90 lineto 93.75 82.5 lineto stroke restore")
            .width(120)
            .height(120);
    DeviceType customPChannelMosfet = pChannelMosfetBuilder.build();
    defaultLibraryBuilder.addDeviceType(pChannelMosfetBuilder.model("Ideal")
        .parameterdefault("Threshold Voltage", "-1V").parameterdefault("Transconductance", "1e-5")
        .build());


    // Digital Symbols for Mixed Mode Simulation
    DeviceTypeBuilder notGateBuilder =
        new DeviceTypeBuilder()
            .type("NOT Gate")
            .prefix("U")
            .port("A", -80, 0)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} DAC_BRIDGE(out_low={%Output Low} "
                    + "out_high={%Output High} " + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} ADC_BRIDGE(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} D_INVERTER(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} " + "input_load={%Input Load})\n"
                    + "A{%SPICENAME}_GATE {%SPICENAME}_id1 {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "A{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "A{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 135 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 25 50 moveto 0 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 125 50 moveto 25 0 lineto 25 100 lineto closepath 125 50 moveto stroke restore 1 J 1 j save 1 0 0 -1 0 100 6 array astore concat 133.242 53.809 moveto 131.137 55.598 127.98 55.344 126.191 53.242 curveto 124.402  51.137 124.656 47.98 126.758 46.191 curveto 128.863 44.402 132.02 44.656 133.809 46.758 curveto 135.59 48.855 135.348 51.992 133.262 53.789 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customNotGate = notGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(notGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder andGateBuilder =
        new DeviceTypeBuilder()
            .type("AND Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_and(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 90.129 0 moveto 117.742 0.07 140.07 22.516 140 50.129 curveto 139.93 77.648  117.633 99.938 90.113 100 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 90 0 moveto 20 0 lineto 20 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 100 moveto 90 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 140 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 70 moveto 0 70 lineto stroke restore")
            .width(160)
            .height(100);
    DeviceType customAndGate = andGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(andGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder nandGateBuilder =
        new DeviceTypeBuilder()
            .type("NAND Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_nand(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 90.129 0 moveto 117.742 0.07 140.07 22.516 140 50.129 curveto 139.93 77.648  117.633 99.938 90.113 100 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 90 0 moveto 20 0 lineto 20 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 100 moveto 90 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 150 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 70 moveto 0 70 lineto stroke restore 1 J 1 j save 1 0 0 -1 0 100 6 array astore concat 148.242 53.809 moveto 146.137 55.598 142.98 55.344 141.191 53.242 curveto 139.402  51.137 139.656 47.98 141.758 46.191 curveto 143.863 44.402 147.02 44.656  148.809 46.758 curveto 150.59 48.855 150.348 51.992 148.262 53.789 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customNandGate = nandGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(nandGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder orGateBuilder =
        new DeviceTypeBuilder()
            .type("OR Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_or(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 20 100 moveto 90 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 140 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 25 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 25 70 moveto 0 70 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 0 moveto 90 0 lineto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 53.674 0.164 moveto 62.48 2.406 69.042 26.535 68.319 54.059 curveto 67.627 80.676  60.362 100.891 51.818 99.969 curveto stroke restore 0 J 0 j save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 0 90 0 90 0 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 100 90 100 90 100 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customOrGate = orGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(orGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder norGateBuilder =
        new DeviceTypeBuilder()
            .type("NOR Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_nor(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 14.863 100 moveto 84.863 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 144.863 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 20 70 moveto 0 70 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 14.863 0 moveto 84.863 0 lineto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 39.889 0.164 moveto 48.694 2.406 55.257 26.535 54.534 54.059 curveto 53.842 80.676  46.577 100.891 38.033 99.969 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140.27 45.016 moveto 143.023 45.238 145.07 47.652 144.848 50.402 curveto 144.621  53.156 142.211 55.207 139.457 54.98 curveto 136.707 54.758 134.656 52.344  134.879 49.594 curveto 135.094 46.945 137.344 44.926 140 45 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 135 50 moveto 115 0 85 0 85 0 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 135 50 moveto 115 100 85 100 85 100 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customNorGate = norGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(norGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder xorGateBuilder =
        new DeviceTypeBuilder()
            .type("XOR Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_xor(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 30 100 moveto 90 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 140 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 15 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 15 70 moveto 0 70 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 30 0 moveto 90 0 lineto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 80.511 0 moveto 89.317 2.242 95.879 26.371 95.156 53.895 curveto 94.464 80.512  87.199 100.727 78.655 99.805 curveto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 28.692 0.195 moveto 37.498 2.438 44.05 26.566 43.337 54.09 curveto 42.646 80.707 35.381 100.922 26.837 100 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 0 90 0 90 0 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 100 90 100 90 100 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customXorGate = xorGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(xorGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    DeviceTypeBuilder xnorGateBuilder =
        new DeviceTypeBuilder()
            .type("XNOR Gate")
            .prefix("U")
            .port("A", -80, 20)
            .port("B", -80, -20)
            .port("Y", 80, 0)
            .parameter("Input Low", voltageValidator)
            .parameter("Input High", voltageValidator)
            .parameter("Output Low", voltageValidator)
            .parameter("Output High", voltageValidator)
            .parameter("Output Undef", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Input Load", capacitanceValidator)
            .spiceTemplate(
                ".model {%MODELNAME.DAC} dac_bridge(out_low={%Output Low} "
                    + "out_high={%Output High} "
                    + "out_undef={%Output Undef} "
                    + "input_load=0 t_rise=0 t_fall=0)\n"
                    + ".model {%MODELNAME.ADC} adc_bridge(in_low={%Input Low} "
                    + "in_high={%Input High})\n"
                    + ".model {%MODELNAME.GATE} d_xnor(rise_delay={%Rise Time} "
                    + "fall_delay={%Fall Time} "
                    + "input_load={%Input Load})\n"
                    + "a{%SPICENAME}_GATE [{%SPICENAME}_id1 {%SPICENAME}_id2] {%SPICENAME}_od {%MODELNAME.GATE}\n"
                    + "a{%SPICENAME}_i1 [{%A}] [{%SPICENAME}_id1] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_i2 [{%B}] [{%SPICENAME}_id2] {%MODELNAME.ADC}\n"
                    + "a{%SPICENAME}_o [{%SPICENAME}_od] [{%Y}] {%MODELNAME.DAC}")
            .labelTemplate("{%Name}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 100 6 array astore concat 30 100 moveto 90 100 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 160 50 moveto 150 50 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 15 30 moveto 0 30 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 15 70 moveto 0 70 lineto stroke restore save 1 0 0 -1 0 100 6 array astore concat 30 0 moveto 90 0 lineto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 80.511 0 moveto 89.317 2.242 95.879 26.371 95.156 53.895 curveto 94.464 80.512  87.199 100.727 78.655 99.805 curveto stroke restore save 0.372621 0 0 -1 0 100 6 array astore concat 28.692 0.195 moveto 37.498 2.438 44.05 26.566 43.337 54.09 curveto 42.646 80.707 35.381 100.922 26.837 100 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 0 90 0 90 0 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 140 50 moveto 120 100 90 100 90 100 curveto stroke restore save 1 0 0 -1 0 100 6 array astore concat 145.406 45.016 moveto 148.16 45.242 150.207 47.652 149.984 50.406 curveto 149.758  53.16 147.348 55.207 144.594 54.984 curveto 141.84 54.758 139.793 52.348 140.016 49.594 curveto 140.242 46.848 142.645 44.797 145.395 45.016 curveto stroke restore")
            .width(160)
            .height(100);
    DeviceType customXnorGate = xnorGateBuilder.build();
    defaultLibraryBuilder.addDeviceType(xnorGateBuilder.model("Ideal CMOS")
        .parameterdefault("Input Low", "2.5V").parameterdefault("Input High", "2.5V")
        .parameterdefault("Output Low", "0.0V").parameterdefault("Output High", "5.0V")
        .parameterdefault("Output Undef", "2.2V").parameterdefault("Rise Time", "0ps")
        .parameterdefault("Fall Time", "0ps").parameterdefault("Input Load", "0F").build());

    // Square Wave Source
    DeviceTypeBuilder squareWaveVoltageSourceBuilder =
        new DeviceTypeBuilder()
            .type("Square Wave Voltage")
            .prefix("V")
            .port("1", 60, 0)
            .port("0", -60, 0)
            .parameter("Low Voltage", voltageValidator)
            .parameter("High Voltage", voltageValidator)
            .parameter("Rise Time", timeValidator)
            .parameter("Fall Time", timeValidator)
            .parameter("Pulse Width", timeValidator)
            .parameter("Period", timeValidator)
            // The spice template intentionally swaps high and low voltages so that it's low first.
            .spiceTemplate(
                "V{%SPICENAME} {%0} {%1} PULSE ({%High Voltage} {%Low Voltage} {%Pulse Width} {%Rise Time} {%Fall Time} {%Pulse Width} {%Period})")
            .labelTemplate("{%Name}\n{%High Voltage} T={%Period}")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 120 6 array astore concat 40 70 moveto 40 50 lineto 60 50 lineto 60 70 lineto 80 70 lineto 80 50 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 10 60 moveto 0 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 110 60 moveto 120 60 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 5 45 moveto 5 35 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 0 40 moveto 10 40 lineto stroke restore save 1 0 0 -1 0 120 6 array astore concat 120 40 moveto 110 40 lineto stroke restore 10 60 moveto 10 32.387 32.387 10 60 10 curveto 87.613 10 110 32.387 110 60 curveto 110 87.613 87.613 110 60 110 curveto 32.387 110 10 87.613 10 60 curveto closepath 10 60 moveto stroke")
            .width(120)
            .height(120);
    DeviceType customSquareWaveVoltageSourceBuilder = squareWaveVoltageSourceBuilder.build();
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=1us")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "0.5us").parameterdefault("Period", "1us").build());
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=2us")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "1us").parameterdefault("Period", "2us").build());
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=4us")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "2us").parameterdefault("Period", "4us").build());
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=1ms")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "0.5ms").parameterdefault("Period", "1ms").build());
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=2ms")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "1ms").parameterdefault("Period", "2ms").build());
    defaultLibraryBuilder.addDeviceType(squareWaveVoltageSourceBuilder.model("5V T=4ms")
        .parameterdefault("Low Voltage", "0V").parameterdefault("High Voltage", "5V")
        .parameterdefault("Rise Time", "0s").parameterdefault("Fall Time", "0s")
        .parameterdefault("Pulse Width", "2ms").parameterdefault("Period", "4ms").build());

    // Analysis Devices are also not really spice devices.
    // They provide a simple way to set simulation parameters
    // for the time being to test the parser.
    DeviceTypeBuilder groundBuilder =
        new DeviceTypeBuilder()
            .type("Ground")
            .prefix("")
            .port("0", 0, -40)
            .spiceTemplate("")
            .draw("0 0 1 setrgbcolor save 1 0 0 -1 0 80 6 array astore concat 10 40 moveto 70 40 lineto stroke restore save 1 0 0 -1 0 80 6 array astore concat 30 70 moveto 50 70 lineto stroke restore save 1 0 0 -1 0 80 6 array astore concat 20 55 moveto 60 55 lineto stroke restore save 1 0 0 -1 0 80 6 array astore concat 40 40 moveto 40 0 lineto stroke restore")
            .width(80)
            .height(80);
    DeviceType customGround = groundBuilder.build();
    defaultLibraryBuilder.addDeviceType(groundBuilder.build());

    DeviceTypeBuilder voltageProbeBuilder =
        new DeviceTypeBuilder()
            .type("Voltage Probe")
            .prefix("Probe ")
            .port("0", -20, 0)
            .parameter("Color", colorValidator)
            .parameter("Group", textValidator)
            .labelTemplate("{%Name}")
            // Using a floating resistor to measure voltage is undocumented but seems to work.
            // This format is parsed in ngspice.py after simulation
            .spiceTemplate("R{%SPICENAME} {%0} name_{$Name}|color_{$Color}|group_{$Group}|{%SPICENAME} 0")
            .draw("save 0 1 1 0 0 40 6 array astore concat -35 10 moveto -5 10 lineto -5 40 lineto -35 40 lineto closepath -35 10 moveto stroke restore save 1 0 0 -1 0 40 6 array astore concat 10 30 moveto 0 20 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 15 10 moveto 25 30 lineto 35 10 lineto stroke restore")
            .width(40)
            .height(40);
    DeviceType customVoltageProbe = voltageProbeBuilder.build();
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Blue")
      .parameterdefault("Color", "blue").parameterdefault("Group", "voltage1").build());
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Red")
        .parameterdefault("Color", "red").parameterdefault("Group", "voltage1").build());
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Orange")
        .parameterdefault("Color", "orange").parameterdefault("Group", "voltage1").build());
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Yellow")
        .parameterdefault("Color", "yellow").parameterdefault("Group", "voltage1").build());
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Green")
        .parameterdefault("Color", "green").parameterdefault("Group", "voltage1").build());
    defaultLibraryBuilder.addDeviceType(voltageProbeBuilder.model("Purple")
        .parameterdefault("Color", "purple").parameterdefault("Group", "voltage1").build());

    DeviceTypeBuilder currentProbeBuilder =
        new DeviceTypeBuilder().type("Current Probe")
            .prefix("Probe ")
            .port("0", -20, 0)
            .port("1", 20, 0)
            .parameter("Color", colorValidator)
            .parameter("Group", textValidator)
            .labelTemplate("{%Name}")
            // Using 0V DC source is the documented ngspice way of measuring current.
            // This format is parsed in ngspice.py after simulation
            .spiceTemplate("Vname_{$Name}|color_{$Color}|group_{$Group}|{%SPICENAME} {%0} {%1}")
            .draw("save 1 0 0 -1 0 40 6 array astore concat 5 5 30 30 exch dup neg 3 1 roll 5 3 roll moveto 0 rlineto 0 exch rlineto 0 rlineto closepath stroke restore save 1 0 0 -1 0 40 6 array astore concat 15 15 moveto 25 15 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 20 15 moveto 20 25 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 15 25 moveto 25 25 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 0 20 moveto 5 15 lineto stroke restore save 1 0 0 -1 0 40 6 array astore concat 40 20 moveto 35 15 lineto stroke restore")
            .width(40).height(40);
    DeviceType customCurrentProbe = currentProbeBuilder.build();
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Blue")
      .parameterdefault("Color", "blue").parameterdefault("Group", "current1").build());
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Red")
        .parameterdefault("Color", "red").parameterdefault("Group", "current1").build());
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Orange")
        .parameterdefault("Color", "orange").parameterdefault("Group", "current1").build());
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Yellow")
        .parameterdefault("Color", "yellow").parameterdefault("Group", "current1").build());
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Green")
        .parameterdefault("Color", "green").parameterdefault("Group", "current1").build());
    defaultLibraryBuilder.addDeviceType(currentProbeBuilder.model("Purple")
        .parameterdefault("Color", "purple").parameterdefault("Group", "current1").build());
    
    defaultLibraryBuilder.addCategory("Passive",
        Arrays.asList(customResistor, customCapacitor, customInductor), "MultiMulti");
    defaultLibraryBuilder.addCategory("Sources", Arrays.asList(customDCVoltageSource,
        customACVoltageSource, customDCCurrentSource, customACCurrentSource,
        customSquareWaveVoltageSourceBuilder, customGround), "MultiMulti");
    defaultLibraryBuilder.addCategory("Diodes", Arrays.asList(customDiode), "Single");
    defaultLibraryBuilder.addCategory("Transistors", Arrays.asList(customPNPTransistor,
        customNPNTransistor, customNChannelMosfet, customPChannelMosfet), "MultiMulti");
    defaultLibraryBuilder.addCategory("Logic", Arrays.asList(customNotGate, customAndGate,
        customNandGate, customOrGate, customNorGate, customXorGate, customXnorGate), "MultiSingle");
    defaultLibraryBuilder.addCategory("Probes",
        Arrays.asList(customVoltageProbe, customCurrentProbe), "MultiMulti");

    defaultLibrary = defaultLibraryBuilder.build();
  }

  @Override
  public ImmutableList<String> getCategories() {
    return defaultLibrary.getCategories();
  }

  @Override
  public ImmutableList<DeviceType> getDeviceTypes(String category) {
    return defaultLibrary.getDeviceTypes(category);
  }

  @Override
  public String getDeviceDisplay(String category) {
    return defaultLibrary.getDeviceDisplay(category);
  }

  @Override
  public ImmutableList<DeviceType> getDeviceTypeModels(DeviceType deviceType) {
    return defaultLibrary.getDeviceTypeModels(deviceType);
  }

  public DeviceLibrary getDefaultLibrary() {
    return defaultLibrary;
  }
}
