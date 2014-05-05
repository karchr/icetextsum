package is.ru.nlp.textsum.unsupervised;

import is.ru.nlp.textsum.NLPUtil.Language;

/**
 * @author Karin Christiansen
 *
 */


public enum Type {
	
	BASELINE() {
		@Override
        public String summary() {
                return "summary";
		}
		@Override
        public String keyword() {
			throw new UnsupportedOperationException("Keyword Extraction not Supported for Baseline");
        }
		@Override
		public Language icelandic() {
			return Language.ICELANDIC;
		}
		@Override
		public Language english() {
			return Language.ENGLISH;
		}
		@Override
		public boolean supportsLemmatized(){
			return false;
		}
		@Override
		public boolean supportsLanguage(String language) {
			return true; //Language independed, supports all languages
		}
    }, 
	
	TFXIDF() {	
		@Override
        public String summary() {
                return "summary";
		}
		@Override
        public String keyword() {
			throw new UnsupportedOperationException("Keyword Extraction not Supported for TFxIDF");
        }
		@Override
		public Language icelandic() {
			return Language.ICELANDIC;
		}
		@Override
		public Language english() {
			throw new UnsupportedOperationException("English not Supported for TFxIDF Summaries");
		}
		@Override
		public boolean supportsLanguage(String language) {
			if (icelandic().name().equals(language)) return true;
			else return false;
		}
		@Override
		public boolean supportsLemmatized() {
			return true;
		}
    }, 
    
	LLR() {	
		@Override
        public String summary() {
                return "summary";
		}
		@Override
        public String keyword() {
			throw new UnsupportedOperationException("Keyword Extraction not Supported for LLR");
        }
		@Override
		public Language icelandic() {
			return Language.ICELANDIC;
		}
		@Override
		public Language english() {
			throw new UnsupportedOperationException("English not Supported for LLR Summaries");
		}
		@Override
		public boolean supportsLanguage(String language) {
			if (icelandic().name().equals(language)) return true;
			else return false;
		}
		@Override
		public boolean supportsLemmatized() {
			return true;
		}
    }, 
	
	TEXTRANK() {
		@Override
        public String summary() {
            return "summary";
		}
		@Override
        public String keyword() {
            return "keyword";
        }
		@Override
		public Language icelandic() {
			return Language.ICELANDIC;
		}
		@Override
		public Language english() {
			return Language.ENGLISH;
		}
		@Override
		public boolean supportsLemmatized(){
			return false;
		}
		@Override
		public boolean supportsLanguage(String language) {
			if (icelandic().name().equals(language) || english().name().equals(language)) return true;
			else return false;
		}
	};
	
	//public abstract String name();
	public abstract String summary();
	public abstract String keyword();
	public abstract Language icelandic();
	public abstract Language english();	
	public abstract boolean supportsLanguage(String language);	
	public abstract boolean supportsLemmatized();
}
