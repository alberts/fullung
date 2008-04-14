function []=readmlf(filename)
fp = fopen(filename, 'r');
while ~feof(fp)
    line = fgetl(fp);
    [start, stop, phoneme, score] = strread(line, '%d%d%s%f', 'delimiter', ' ');
end
fclose(fp);
