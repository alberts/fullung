function fusefile(inputfile,outputfile)
if strcmp(inputfile,outputfile)
    error('Input and output files are the same');
end

load fusion.mat finalW systems sidestages;

fid = fopen(inputfile);
C = textscan(fid,'%s%s%s%s%s%f','CollectOutput',0);
fclose(fid);

fid = fopen(outputfile,'w');
scores = [C{6}]';
trialcount = size(scores, 2);
for i=1:trialcount
    key = C{1}{i};
    answer = C{5}{i};

    sideinfos = {[], [], []};
    lang = C{3}{i};
    if strcmp(lang,'eng:eng')
        sideinfos{1} = [0 0 1]';
    elseif strcmp(lang,'eng:oth')||strcmp(lang,'oth:eng')
        sideinfos{1} = [0 1 0]';        
    elseif strcmp(lang,'oth:oth')
        sideinfos{1} = [1 0 0]';
    else
        error('Invalid language: %s', channel);
    end

    channel = C{4}{i};
    if strcmp(channel,'phn:phn')
        sideinfos{2} = [0 0 1]';
    elseif strcmp(channel,'mic:phn')||strcmp(channel,'phn:mic')
        sideinfos{2} = [0 1 0]';        
    elseif strcmp(channel,'mic:mic')
        sideinfos{2} = [1 0 0]';
    else
        error('Invalid channel: %s', channel);
    end
    
    gender = C{2}{i};
    if strcmp(gender,'m')
        sideinfos{3} = [0 1]';
    elseif strcmp(gender,'f')
        sideinfos{3} = [1 0]';
    else
        error('Invalid gender: %s', gender);
    end

    trial_scores = [scores(:,i); sideinfos{1}; sideinfos{2}; sideinfos{3}];
    score = apply_bilinear_fusions(finalW, trial_scores, systems, sidestages);
    output = sprintf('%s %s %s %s %s %.15E\n',key,gender,lang,channel,answer,score);
    fprintf(output);
    fprintf(fid,output);
end
fclose(fid);
