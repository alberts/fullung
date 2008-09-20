function [wind_direction] = uvgrd2dir(ugrd, vgrd)
%
% UVGRD2DIR calculates wind direction from UGRD and VGRD parameters.
%
% Source:
%   http://code.google.com/p/rubysail/wiki/DataDescription
%

sign = (ugrd >= 0 & vgrd >= 0) | (ugrd < 0 & vgrd < 0);
wind_direction = pi - sign .* acos(ugrd./vgrd);
