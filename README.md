simple-hue
======
A simple Java program to control your Philips Hue lights

I designed this program as a simple way to toggle a single Hue light (namely, a lightstrip situated behined my TV). I didn't like the other options out there as they didn't offer quite what I was after. This program has a singular purpose - toggle the selected Hue light(s) on/off. That's it!

If you would like to request additional features, suggest improvements, etc. please feel free to email me [here](mailto:admin@fullcirclegfx.com)

![alt text][image1]
---
Notes
------

This program has been tested on Mac OSX 10.10 and Windows 8.1. You shouldn't have any trouble running on Linux either. The program will run an initial discovery and user registration phase, after which you will be presented with a configuration screen to determine which lights should be toggled, and what color they should be set to when turned on.

After building the configuration file the program will run silently (it will load the config, and determine whether to turn lights on or off, and to the color chosen).

If you would like to re-run the setup process simply deleted the associated **config** file.

![alt text][image4]

Credit
------
A huge thanks to [Blodjer](https://github.com/Blodjer) for his [HueImmersive](https://github.com/Blodjer/HueImmersive) program, which helped me get a huge jump start on this project, and also the [PhilipsHueSDK](https://github.com/PhilipsHue/PhilipsHueSDK-iOS-OSX) for the RGB to XY conversion formula.

[image1]: https://lh4.googleusercontent.com/LGI_0tvHtt9fEBrUxZd3RoEEfMrEWPVR1pAS9G9RX-BWuG4BOVLWcLpO7rKWFvDNRHuzNg=w1602-h730 "simple hue main screen"
[image2]: https://lh3.googleusercontent.com/ncXXmeSC2OHve5c0zZ1vutyejlSVrzIX3xgEF5DWbYPJ87Xi0rqb0omBsPWPTU0UqHsVKw=w1602-h730 "simple hue login screen"
[image3]: https://lh5.googleusercontent.com/5A3mwH2F4C0vcEcA-EZuyEjreTKEC0G4X7RaUMRbBXIoJERMdX0DBxN97krmzZGnHfbjww=w1602-h730 "simple hue config screen"
[image4]: https://lh4.googleusercontent.com/BkTQ-CS3F4eJB1JsuJyWCHn9HST8mkSKgLqenx5Azyx0GewcBNOXwYzgMVeLlsAopukdsg=w1602-h730 "simple hue config screen2"
[image5]: https://lh3.googleusercontent.com/3dVP4fkuVlV_BktIrk_tOjADinEK4dRNXGT186HBISf8Cxk7vYWc42T4i1AAO8e3ZeeYsw=w1602-h730 "simple hue saved config screen"
