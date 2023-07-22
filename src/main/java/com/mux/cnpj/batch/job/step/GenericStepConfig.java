package com.mux.cnpj.batch.job.step;

// import lombok.extern.slf4j.Slf4j;

// @Configuration
// @Slf4j
public class GenericStepConfig<From, To> {
	// private static final String BEANS_NAME_PREFIX = "companiesImport";
	// private static final String STEP_BEAN_NAME = BEANS_NAME_PREFIX + "Step";
	// private static final String RESOURCE_BEAN_NAME = BEANS_NAME_PREFIX + "CsvResource";
	// private static final String PROCESSOR_BEAN_NAME = BEANS_NAME_PREFIX + "Processor";
	// private static final String RESOURCE_LOCATION_PATTERN = "file:csv/Empresas*.zip";

	// private static final int[] INCLUDED_FIELDS = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	// private static final String[] COLUMN_NAMES = new String[] {
	// 		"cnpj_col1_A",
	// 		"nome_col2_B",
	// 		"codNaturezaJuridica_col3_C",
	// 		"motivoSituacaoCadastral_col4_D",
	// 		"socialCapital_col5_E",
	// 		"codPorteEmpresa_col6_F",
	// 		"estadoEUnidadeFederativa_col7_G"
	// };

	// private final TypeToken<From> typeToken = new TypeToken<From>(getClass()) {
	// };
	// private final Type type = typeToken.getType();

	// @SuppressWarnings("unchecked")
	// Class<From> clazz = (Class<From>) CompanyCsv.class;

	// @Autowired
	// @Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	// DataSource cnpjDataSource;

	// @Autowired
	// ZIPBufferedReaderFactory zipBufferedReaderFactory;

	// @Autowired
	// PlatformTransactionManager transactionManager;

	// @Bean
	// @Qualifier(RESOURCE_BEAN_NAME)
	// public Resource[] getResources() {
	// 	Resource[] resources = new Resource[0];
	// 	PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	// 	try {
	// 		resources = resolver
	// 				.getResources(RESOURCE_LOCATION_PATTERN);
	// 	} catch (IOException e) {
	// 		log.error("error loading resources", e);
	// 	}
	// 	return resources;
	// }

	// @Qualifier(RESOURCE_BEAN_NAME)
	// Resource[] resources;

	// @Qualifier(PROCESSOR_BEAN_NAME)
	// ItemProcessor<From, To> processor;

	// // @Bean
	// // public Step companiesImportStep(
	// // 		JobRepository jobRepository) {
	// // 			JpaItemWriter<To> writter = new JpaItemWriter<>();
	// // 			writter.setEntityManagerFactory(entityManagerFactory);
	// // 	Step step = new StepBuilder(STEP_BEAN_NAME, jobRepository)
	// // 			.<From, To>chunk(100, transactionManager)
	// // 			.reader(multiResourceItemReader(resources))
	// // 			.processor(processor)
	// // 			.writer(writter)
	// // 			// .writer(jpaWritter())
	// // 			.build();
	// // 	return step;
	// // }

	// // 	@Bean
	// // public ItemProcessor<CompanyCsv, Company> processor() {
	// // 	return new ItemProcessor<CompanyCsv, Company>() {

	// // 		@Override
	// // 		@Nullable
	// // 		public Company process(@NonNull CompanyCsv csv) throws Exception {
	// // 			Company company = Company.builder()
	// // 					.cnpj(CsvFormatter.toInteger(csv.getCnpj_col1_A().trim()))
	// // 					.name(csv.getNome_col2_B().trim())
	// // 					.legalNature(CsvFormatter.toInteger(csv.getCodNaturezaJuridica_col3_C().trim()))
	// // 					.closeDownReason(CsvFormatter.toInteger(csv.getMotivoSituacaoCadastral_col4_D().trim()))
	// // 					.socialCapital(CsvFormatter.toBigDecimal(csv.getSocialCapital_col5_E()))
	// // 					.companySize(CsvFormatter.toInteger(csv.getCodPorteEmpresa_col6_F().trim()))
	// // 					.build();

	// // 			return company;
	// // 		}
	// // 	};
	// // }

	// @Autowired
	// EntityManagerFactory entityManagerFactory;

	// @Bean
	// public JpaItemWriter<To> jpaWritter() {
	// 	return new JpaItemWriterBuilder<To>().entityManagerFactory(entityManagerFactory).build();
	// }

	// // @Bean
	// // public JdbcBatchItemWriter<To> ToWriter() {
	// // 	JdbcBatchItemWriter<To> itemWriter = new JdbcBatchItemWriter<To>();
	// // 	itemWriter.setDataSource(cnpjDataSource);
	// // 	String sql = new StringBuilder(
	// // 			"INSERT INTO cnpj.To (cnpj, name, col3, col4, socialCapital, col6, col7) VALUES (:cnpj, :name, :col3, :col4, :socialCapital, :col6, :col7)")
	// // 			.toString();
	// // 	itemWriter.setSql(
	// // 			sql);
	// // 	itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<To>());
	// // 	return itemWriter;
	// // }

	// @Bean
	// @StepScope
	// public MultiResourceItemReader<From> multiResourceItemReader(
	// 		@Qualifier(RESOURCE_BEAN_NAME) Resource[] resources) {
	// 	MultiResourceItemReader<From> reader = new MultiResourceItemReader<>();

	// 	log.info("Found {} resources to process", resources.length);
	// 	reader.setName("multiResourceItemReader");
	// 	reader.setResources(resources);
	// 	reader.setDelegate(reader());
	// 	return reader;
	// }

	// @Bean
	// public LineMapper<From> lineMapper() {
	// 	DefaultLineMapper<From> lineMapper = new DefaultLineMapper<From>();
	// 	DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
	// 	lineTokenizer.setDelimiter(";");
	// 	lineTokenizer.setNames(COLUMN_NAMES);
	// 	lineTokenizer.setIncludedFields(INCLUDED_FIELDS);
	// 	BeanWrapperFieldSetMapper<From> fieldSetMapper = new BeanWrapperFieldSetMapper<From>();
	// 	fieldSetMapper.setTargetType(clazz);
	// 	lineMapper.setLineTokenizer(lineTokenizer);
	// 	lineMapper.setFieldSetMapper(fieldSetMapper);
	// 	return lineMapper;
	// }

	// @Bean
	// public FlatFileItemReader<From> reader() {
	// 	FlatFileItemReader<From> itemReader = new FlatFileItemReader<From>();
	// 	itemReader.setLineMapper(lineMapper());
	// 	itemReader.setLinesToSkip(0);
	// 	itemReader.setEncoding(StandardCharsets.ISO_8859_1.toString());
	// 	itemReader.setBufferedReaderFactory(zipBufferedReaderFactory);

	// 	return itemReader;
	// }
}
