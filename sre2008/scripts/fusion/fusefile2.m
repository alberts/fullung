function fusefile2(systems,trnfile,inputfile,outputfile)
if strcmp(inputfile,outputfile)
    error('Input and output files are the same');
end

% language type
cal1 = 11:13;
% gender
cal2 = 14:15;
% channel type
fuse = 8:10;

format = '%s%s%s%s%s%f%f%f%f%f%f%f';
fid = fopen(trnfile);
train = textscan(fid,format,'CollectOutput',0);
fclose(fid);
fid = fopen(inputfile);
input = textscan(fid,format,'CollectOutput',0);
fclose(fid);

trainscores = zeros(7 + 3 + 3 + 2, length(train{1}));
trainscores(1:7, :) = [train{6:length(train)}]';
inputscores = zeros(7 + 3 + 3 + 2, length(input{1}));
inputscores(1:7, :) = [input{6:length(train)}]';

targets = logical(strcmp('target', train{5}));
nontargets = logical(strcmp('nontarget', train{5}));

trainscores(8,logical(strcmp('phn:phn',train{4}))) = 1;
inputscores(8,logical(strcmp('phn:phn',input{4}))) = 1;
trainscores(9,logical(strcmp('phn:mic',train{4}))) = 1;
inputscores(9,logical(strcmp('phn:mic',input{4}))) = 1;
trainscores(9,logical(strcmp('mic:phn',train{4}))) = 1;
inputscores(9,logical(strcmp('mic:phn',input{4}))) = 1;
trainscores(10,logical(strcmp('mic:mic',train{4}))) = 1;
inputscores(10,logical(strcmp('mic:mic',input{4}))) = 1;

trainscores(11,logical(strcmp('eng:eng',train{3}))) = 1;
inputscores(11,logical(strcmp('eng:eng',input{3}))) = 1;
trainscores(12,logical(strcmp('oth:eng',train{3}))) = 1;
inputscores(12,logical(strcmp('oth:eng',input{3}))) = 1;
trainscores(12,logical(strcmp('eng:oth',train{3}))) = 1;
inputscores(12,logical(strcmp('eng:oth',input{3}))) = 1;
trainscores(13,logical(strcmp('oth:oth',train{3}))) = 1;
inputscores(13,logical(strcmp('oth:oth',input{3}))) = 1;

trainscores(14,logical(strcmp('m',train{2}))) = 1;
inputscores(14,logical(strcmp('m',input{2}))) = 1;
trainscores(15,logical(strcmp('f',train{2}))) = 1;
inputscores(15,logical(strcmp('f',input{2}))) = 1;

tar = trainscores(systems,targets);
sidetar = {trainscores(cal1,targets),trainscores(cal2,targets),trainscores(fuse,targets)};
non = trainscores(systems,nontargets);
sidenon = {trainscores(cal1,nontargets),trainscores(cal2,nontargets),trainscores(fuse,nontargets)};

test = inputscores(systems,:);
sidetest = {inputscores(cal1,:),inputscores(cal2,:),inputscores(fuse,:)};
[test,ignore,ignore] = calfusechain(tar,non,sidetar,sidenon,test,sidetest);

decision_threshold = 2.2925;
clip_threshold = 20;

fid = fopen(outputfile,'w');
for i=1:length(input{1})
    score = test(i);
    if score < -clip_threshold
        score = -clip_threshold;
    elseif score > clip_threshold
        score = clip_threshold;
    end
    key = input{1}{i};
    parts = strsplit('_', key);
    model = parts{1};
    parts = strsplit(':', parts{2});
    segment = parts{1};
    channel = parts{2};
    answer = input{5}{i};
    lang = input{3}{i};
    chntype = input{4}{i};
    gender = input{2}{i};
    if 1
        output = sprintf('%s_%s:%s %s %s %s %s %+.15E\n', model, segment, channel, gender, lang, chntype, answer, score);
    else
        if score >= decision_threshold
            decision = 't';
        else
            decision = 'f';
        end
        output = sprintf('short2 n short3 %s %s %s %s %s %+.15E\n', gender, model, segment, channel, decision, score);
    end
    fprintf(output);
    fprintf(fid, output);
end
fclose(fid);
