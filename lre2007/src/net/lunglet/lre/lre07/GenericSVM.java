package net.lunglet.lre.lre07;

public final class GenericSVM {
//    private static Map<String, String> readTrainLabels() throws IOException {
//        Map<String, String> dataLabels = new HashMap<String, String>();
//        BufferedReader reader = new BufferedReader(new FileReader("c:/home/albert/LRE2007/keysetc/albert/output/frontend_0_0.txt"));
//        String line = reader.readLine();
//        while (line != null) {
//            String[] parts = line.split("\\s+");
//            String label = parts[0];
//            String[] idparts = parts[1].split(",");
//            dataLabels.put("/" + idparts[0] + "/" + idparts[1], label);
//            line = reader.readLine();
//        }
//        reader.close();
//        return dataLabels;
//    }
//
//    private static int[] readGramOrder() {
//        H5File gramh5 = new H5File("F:/ngrams_gram.h5", H5File.H5F_ACC_RDONLY);
//        DataSet ds = gramh5.getRootGroup().openDataSet("/gram");
//        Attribute attr = ds.openAttribute("order");
//        DataSpace space = attr.getSpace();
//        int[] order = new int[(int) space.getDim(0)];
//        space.close();
//        attr.read(order);
//        attr.close();
//        ds.close();
//        return order;
//    }
//
//    public static void main(final String[] args) throws IOException {
//        // read labels for segments
//        Map<String, String> segmentLabels = readTrainLabels();
//
//        // map from labels to integers
//        Map<String, Integer> labelMap = new HashMap<String, Integer>();
//        int labelCount = 0;
//        for (String label : segmentLabels.keySet()) {
//            if (labelMap.containsKey(label)) {
//                continue;
//            }
//            labelMap.put(label, labelCount++);
//        }
//
//        // map indexes to labels via segments
//        final H5File datah5 = new H5File("F:/ngrams.h5", H5File.H5F_ACC_RDONLY);
//        Map<Integer, String> indexLabels = new HashMap<Integer, String>();
//        List<Handle<FloatDenseVector>> dataList = new ArrayList<Handle<FloatDenseVector>>();
//        for (Map.Entry<String, String> entry : segmentLabels.entrySet()) {
//            final String segmentName = entry.getKey();
//            String label = entry.getValue();
//            DataSet dataset = datah5.getRootGroup().openDataSet(segmentName);
//            // TODO rename this attribute to indexes at some point
//            Attribute attr = dataset.openAttribute("id");
//            DataSpace space = attr.getSpace();
//            int[] indexes = new int[(int) space.getDim(0)];
//            space.close();
//            attr.read(indexes);
//            for (int i = 0; i < indexes.length; i++) {
//                final int j = i;
//                int index = indexes[i];
//                indexLabels.put(index, label);
//                dataList.add(new Handle<FloatDenseVector>() {
//                    @Override
//                    public FloatDenseVector get() {
//                        DataSet vecds = datah5.getRootGroup().openDataSet(segmentName);
//                        DataSpace vecspace = vecds.getSpace();
//                        long[] dims = vecspace.getDims();
//                        vecspace.close();
//                        FloatDenseVector vec = new FloatDenseVector((int) dims[1]);
////                        DataType dtype = PredefinedType.IEEE_F32LE;
////                        vecds.read(vec.data(), dtype, null, null);
//                        vecds.close();
//                        return vec;
//                    }
//                });
//            }
//            attr.close();
//            dataset.close();
//        }
//
//        @SuppressWarnings("unchecked")
//        Handle<FloatVector<?>>[] data = (Handle<FloatVector<?>>[]) dataList.toArray(new Handle<?>[0]);
//
//        // read order of indexes in gram matrix
//        int[] order = readGramOrder();
//        int gramdim = order.length;
//
//        // map indexes to positions
//        Map<Integer, Integer> indexPositions = new HashMap<Integer, Integer>();
//        for (int i = 0; i < order.length; i++) {
//            indexPositions.put(order[i], i);
//        }
//
////        // map gram positions to labels
////        Map<Integer, String> gramLabels = new HashMap<Integer, String>();
////        for (Map.Entry<Integer, String> entry : indexLabels.entrySet()) {
////            int index = entry.getKey();
////            String label = entry.getValue();
////            gramLabels.put(indexPositions.get(index), label);
////        }
//
//        H5File gramh5 = new H5File("F:/ngrams_gram.h5", H5File.H5F_ACC_RDONLY);
//        HDFReader reader = new HDFReader(gramh5);
//        FloatPackedMatrix gram = FloatPackedMatrix.createSymmetric(gramdim, Storage.DIRECT);
//        // TODO only read parts of the gram matrix here
//        reader.read(gram, "/gram");
//        reader.close();
//
//        int[] labels = new int[0];
//
//        System.out.println(data.length);
//        System.out.println(gram.rows());
//        
////        new SimpleSvm(data, gram, labels);
//
//        datah5.close();
//    }
//
//    public static void main3(final String[] args) {
//        H5File gramh5 = new H5File("F:/ngrams_gram.h5", H5File.H5F_ACC_RDONLY);
//        HDFReader reader = new HDFReader(gramh5);
//        DataSet ds = gramh5.getRootGroup().openDataSet("/gram");
//        DataSpace space = ds.getSpace();
//        long[] dims = space.getDims();
//        if (dims.length != 1) {
//            throw new RuntimeException(new IOException());
//        }
//        int k = (int) dims[0];
//        int n = ((int) Math.sqrt(1 + 8 * k) - 1) / 2;
//        space.close();
//        int[] order = new int[n];
//        Attribute attr = ds.openAttribute("order");
//        attr.read(order);
//        attr.close();
//        ds.close();
//        FloatPackedMatrix gram = FloatPackedMatrix.createSymmetric(n, Storage.DIRECT);
//        reader.read(gram, "/gram");
//        System.out.println(gram.get(0, 0));
//        reader.close();
//        System.out.println(Arrays.toString(order));
//        System.out.println(order.length);
//    }
//
//    public static void main2(final String[] args) throws IOException {
//        List<String> frontendIds = new ArrayList<String>(CalculateGram.readFrontendIds());
//        Collections.sort(frontendIds);
//        System.out.println(frontendIds.size());
//        Map<String, String> dataLabels = readTrainLabels();
//
//        H5File datah5 = new H5File("F:/ngrams.h5", H5File.H5F_ACC_RDONLY);
//        H5File gramh5 = new H5File("F:/ngrams_gram.h5", H5File.H5F_ACC_RDONLY);
//        Group dataRoot = datah5.getRootGroup();
//
//        Map<String, DataVector> data = new HashMap<String, DataVector>();
//        for (Group group : dataRoot.getGroups()) {
//            for (DataSet ds : group.getDataSets()) {
//                String name = ds.getName();
//                if (!dataLabels.containsKey(name)) {
//                    ds.close();
//                    continue;
//                }
//                String label = dataLabels.get(name);
//                DataSpace space = ds.getSpace();
//                Attribute attr = ds.openAttribute("id");
//                int[] ids = new int[(int) space.getDim(0)];
//                attr.read(ids);
//                attr.close();
//                for (int i = 0; i < space.getDims()[0]; i++) {
//                    if (label == null) {
//                        System.out.println(ds.getName());
//                        throw new AssertionError();
//                    }
//                    DataVector vec = new DataVector(ids[i], ds, i, label);
//                }
//                space.close();
//                // don't close dataset here because it will be read later
//            }
//            group.close();
//        }
//
//        gramh5.close();
//        datah5.close();
//    }
}
