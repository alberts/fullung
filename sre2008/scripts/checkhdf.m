function checkhdf(filename)
info = hdf5info(filename);
for group=info.GroupHierarchy.Groups
   for dataset = group.Datasets
      datasetname = dataset.Name;
      disp(datasetname);
      hdf5read(filename, datasetname);
   end
end
