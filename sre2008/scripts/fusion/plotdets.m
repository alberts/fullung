function plotdets(evalfile,linestyle)
disp(evalfile);
fid = fopen(evalfile);
C = textscan(fid,'%s%s%s%s%s%f');
fclose(fid);

scorecount = length(C{1});
tars = {false(1,scorecount), false(1,scorecount), false(1,scorecount), false(1,scorecount)};
nons = {false(1,scorecount), false(1,scorecount), false(1,scorecount), false(1,scorecount)};
for i=1:1:scorecount
    channel = C{4}{i};
    if strcmp(channel, 'phn:phn')
        channel = 1;
    elseif strcmp(channel, 'phn:mic')
        channel = 2;
    elseif strcmp(channel, 'mic:phn')
        channel = 3;
    elseif strcmp(channel, 'mic:mic')
        channel = 4;
    else
        error('Invalid channel type: %s', channel);
    end
    answer = C{5}{i};
    if strcmp(answer,'target')
        tars{channel}(i) = 1;
    elseif strcmp(answer,'nontarget')
        nons{channel}(i) = 1;
    else
        error('Invalid answer: %s', answer);
    end
    if mod(i,5000)==0||i==scorecount
        fprintf('processed %d of %d scores\n', i, scorecount);
    end
end

scores = C{6};
for i=1:1:length(tars)
    tars{i} = scores(tars{i});
    nons{i} = scores(nons{i});
end
tar = [tars{1}; tars{2}; tars{3}; tars{4}];
non = [nons{1}; nons{2}; nons{3}; nons{4}];

colors = 'rgbmk';
descriptions = {'phn-phn', 'phn-mic', 'mic-phn', 'mic-mic'};
hold on;
for i=1:1:length(tars)
    if isempty(tars{i})||isempty(nons{i})
        continue
    end
    fprintf('%s: %d targets, %d nontargets\n', descriptions{i}, length(tars{i}), length(nons{i}));
    plotdet(tars{i}, nons{i}, [colors(i),linestyle]);
end
fprintf('total: %d targets, %d nontargets\n', length(tar), length(non));
plotdet(tar, non, [colors(end),linestyle]);
hold off;
