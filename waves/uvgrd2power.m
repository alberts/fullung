function [wind_power] = uvgrd2power(ugrd, vgrd)
%
% UVGRD2POWER calculates wind power from UGRD and VGRD parameters.
%
% Examples:
%   uvgrd2power(5.750,7.970) = 9.8277
%   uvgrd2power(4.470,8.290) = 9.4183
%
% Source:
%   http://code.google.com/p/rubysail/wiki/DataDescription
%

wind_power = sqrt(ugrd.^2 + vgrd.^2);
