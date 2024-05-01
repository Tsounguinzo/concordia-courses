package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.JsonAdapter;
import courses.concordia.json.serialization.InstructorDepartmentDeserializer;
import courses.concordia.json.serialization.InstructorTagDeserializer;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "instructors")
public class Instructor {
    @MongoId
    private String _id;
    private String firstName;
    private String lastName;
    private Department department;
    private Set<InstructorCourse> courses;
    private Set<Tag> tags;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgDifficulty = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgRating = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private int reviewCount = 0;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InstructorCourse {
        private String subject;
        private String catalog;
    }

    @Getter
    @AllArgsConstructor
    @JsonAdapter(InstructorTagDeserializer.class)
    public enum Tag {
        TOUGH_GRADER("Tough Grader"),
        GET_READY_TO_READ("Get Ready To Read"),
        PARTICIPATION_MATTERS("Participation Matters"),
        EXTRA_CREDIT("Extra Credit"),
        SKIP_CLASS_YOU_WONT_PASS("Skip Class? You Won't Pass"),
        GROUP_PROJECTS("Group Projects"),
        AMAZING_LECTURES("Amazing Lectures"),
        CLEAR_GRADING_CRITERIA("Clear Grading Criteria"),
        GIVES_GOOD_FEEDBACK("Gives Good Feedback"),
        INSPIRATIONAL("Inspirational"),
        LOTS_OF_HOMEWORK("Lots Of Homework"),
        HILARIOUS("Hilarious"),
        BEWARE_OF_POP_QUIZZES("Beware Of Pop Quizzes"),
        SO_MANY_PAPERS("So Many Papers"),
        CARING("Caring"),
        RESPECTED("Respected"),
        FLEXIBLE_DEADLINES("Flexible Deadlines"),
        LECTURE_HEAVY("Lecture Heavy"),
        TEST_HEAVY("Test Heavy"),
        GRADED_BY_FEW_THINGS("Graded By Few Things"),
        ACCESSIBLE_OUTSIDE_CLASS("Accessible Outside Class"),
        ONLINE_SAVVY("Online Savvy"),
        ENGAGING("Engaging"),
        TECHNICALLY_PROFICIENT("Technically Proficient"),
        INDUSTRY_EXPERIENCED("Industry Experienced"),
        RESEARCH_ORIENTED("Research-Oriented"),
        MULTIDISCIPLINARY_APPROACH("Multidisciplinary Approach"),
        INTERACTIVE_SESSIONS("Interactive Sessions"),
        ENCOURAGES_CRITICAL_THINKING("Encourages Critical Thinking"),
        USES_MULTIMEDIA("Uses Multimedia"),
        CULTURALLY_INCLUSIVE("Culturally Inclusive"),
        TESTS_NOT_MANY("Tests? Not many"),
        WOULD_TAKE_AGAIN("Would take again"),
        TESTS_ARE_TOUGH("Tests are tough");

        private final String displayName;

        @JsonValue
        public String toValue() {
            return this.displayName;
        }
        public static Tag fromString(String text) {
            if (text != null) {
                String normalizedText = text
                        .replaceAll("[^A-Za-z0-9]", "")
                        .replaceAll("\\s+", " ")
                        .toUpperCase();
                for (Tag tag : Tag.values()) {
                    String normalizedEnum = tag.displayName
                            .replaceAll("[^A-Za-z0-9]", "")
                            .replaceAll("\\s+", " ")
                            .toUpperCase();
                    if (normalizedEnum.equals(normalizedText)) {
                        return tag;
                    }
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonAdapter(InstructorDepartmentDeserializer.class)
    public enum Department {
        ANDRAGOGY("Andragogy"),
        APPLIED_HUMAN_SCIENCES("Applied Human Sciences"),
        APPLIED_SOCIAL_SCIENCE("Applied Social Science"),
        BI_DISCIPLINE_PROGRAMME("Bi-Discipline Programme"),
        BIOLOGY("Biology"),
        CHEMISTRY_BIOCHEMISTRY("Chemistry and Biochemistry"),
        CLASSICS("Classics"),
        CLASSICS_MOD_LANG_LINGUISTICS("Classics, Mod Lang&Linguistics"),
        COMMUNITY_PUBLIC_AFFAIRS("School of Community&Public Affairs"),
        COMMUNICATION_STUDIES("Communication Studies"),
        ECONOMICS("Economics"),
        EDUCATION("Education"),
        ENGLISH("English"),
        ETUDES_FRANCAISES("Etudes Francaises"),
        HEALTH_KINESIOLOGY_PHYSIOLOGY("Health, Kinesiology, and Applied Physiology"),
        GEOGRAPHY_PLANNING_ENVIRONMENT("Geography, Planning & Environment"),
        GEOLOGY("Geology"),
        HEALTH_EDUCATION("Health Education"),
        HISTORY("History"),
        INTERDISCIPLINARY_STUDIES("Interdisciplinary Studies"),
        IRISH_STUDIES("School of Irish Studies"),
        JOURNALISM("Journalism"),
        LEISURE_STUDIES("Leisure Studies"),
        LIBERAL_ARTS_COLLEGE("Liberal Arts College"),
        LIBRARY_STUDIES("Library Studies"),
        LONERGAN_COLLEGE("Lonergan University College"),
        LC_DIVERSITY_SUSTAINABILITY("LC Diversity & Sustainability"),
        MATHEMATICS_STATISTICS("Mathematics and Statistics"),
        PHILOSOPHY("Philosophy"),
        PHYSICS("Physics"),
        POLITICAL_SCIENCE("Political Science"),
        PSYCHOLOGY("Psychology"),
        RELIGIONS_CULTURES("Religions and Cultures"),
        SCIENCE_COLLEGE("Science College"),
        SIMONE_DEBEAUVOIR("Simone deBeauvoir Institute & Women Studies"),
        SOCIOLOGY_ANTHROPOLOGY("Sociology and Anthropology"),
        TESL("T.E.S.L."),
        THEOLOGICAL_STUDIES("Theological Studies"),
        BUSINESS_ADMINISTRATION("Business Administration"),
        COMMUNICATIONS_DEPARTMENT("Communications Department"),
        COMPUTER_INSTITUTE("Computer Institute"),
        INTENSIVE_ENGLISH_DEPARTMENT("Intensive English Department"),
        NON_INTENSIVE_ENGLISH_DEPARTMENT("Non-Intensive English Department"),
        FRENCH_DEPARTMENT("French Department"),
        CCE_ENGLISH_DEPARTMENT("CCE - English Department"),
        PHOTOGRAPHY_DEPARTMENT("Photography Department"),
        STUDY_SKILLS_DEPARTMENT("Study Skills Department"),
        SPANISH_DEPARTMENT("Spanish Department"),
        TOURISM_DEPARTMENT("Tourism Department"),
        WEB_DEPARTMENT_BUSINESS("Web Department - Business"),
        CCE_WEB_DEPARTMENT_COMMUNICATION_PR("CCE Web Department - Communication & PR"),
        CCE_WEB_DEPARTMENT_COMPUTER_INSTITUTE("CCE Web Department - Computer Institute"),
        CCE_WEB_DEPARTMENT_LANGUAGE_INSTITUTE("CCE Web Department - Language Institute"),
        CCE_WEB_DEPARTMENT_PROFESSIONAL_DEVELOPMENT("CCE Web Department - Professional Development"),
        CCE_WEB_DEPARTMENT_PERSONAL_DEVELOPMENT("CCE Web Department - Personal Development"),
        CCE_WEB_DEPARTMENT_PHOTOGRAPHY("CCE Web Department - Photography"),
        CCE_CORPORATE_TRAINING("CCE Corporate Training"),
        INSTITUTE_FOR_CO_OPERATIVE_EDUCATION("Institute for Co-operative Education"),
        BUILDING_CIVIL_ENVIRON_ENGINEERING("Building Civil & Environ Engineering"),
        BUILDING_STUDIES("Building Studies"),
        CENTRE_FOR_ENGINEER_IN_SOCIETY("Centre for Engineer in Society"),
        CHEMICAL_MATERIALS_ENGINEERING("Chemical and Materials Engineering"),
        CONCORDIA_INSTITUTE_INFORMATION_SYSTEMS_ENGRG("Concordia Institute for Information Systems Engineering"),
        CIVIL_ENGINEERING("Civil Engineering"),
        COMPUTER_SCIENCE_SOFTWARE_ENGINEERING("Computer Science & Software Engineering"),
        ELECTRICAL_COMPUTER_ENGINEERING("Electrical and Computer Engineering"),
        MECHANICAL_INDUSTRIAL_AEROSPACE_ENGINEERING("Mechanical, Industrial and Aerospace Engineering"),
        ART_EDUCATION("Art Education"),
        ART_EDUCATION_ART_THERAPY("Art Education & Art Therapy"),
        ART_HISTORY("Art History"),
        CINEMA("Cinema"),
        CONTEMPORARY_DANCE("Contemporary Dance"),
        CREATIVE_ARTS_THERAPIES("Creative Arts Therapies"),
        DESIGN_COMPUTATION_ARTS("Design and Computation Arts"),
        FINE_ARTS("Fine Arts"),
        MUSIC("Music"),
        PAINTING_DRAWING("Painting and Drawing"),
        PRINTMAKING_PHOTOGRAPHY("Printmaking and Photography"),
        SCULPTURE_CERAMICS_FIBRES("Sculpture, Ceramics & Fibres"),
        STUDIO_ART_I("Studio Art I"),
        STUDIO_ART_II("Studio Art II"),
        STUDIO_ARTS("Studio Arts"),
        THEATRE("Theatre"),
        JOHN_MOLSON_EXECUTIVE_CENTRE("John Molson Executive Centre"),
        ACCOUNTANCY("Accountancy"),
        AVIATION_MBA("Aviation MBA"),
        SUPPLY_CHAIN_BUSINESS_TECHNOLOGY_MANAGEMENT("Supply Chain And Business Technology Management"),
        ECONOMICS_JMSB("Economics - JMSB"),
        EXECUTIVE_MBA("Executive MBA"),
        FINANCE("Finance"),
        GENERAL_ADMINISTRATION("General Administration"),
        INTERNATIONAL_BUSINESS("International Business"),
        GOODMAN_INVESTMENT_MANAGMENT("Goodman Institute - Investment Management"),
        MANAGEMENT("Management"),
        MARKETING("Marketing"),
        GRADUATE_PROFESSIONAL_SKILLS("Graduate and Professional Skills"),
        SCHOOL_OF_GRADUATE_STUDIES("School of Graduate Studies");

        private final String description;

        @JsonValue
        public String toValue() {
            return this.description;
        }

        public static Department fromString(String text) {
            if (text != null && !text.isEmpty()) {
                String searchText = text.toLowerCase();
                if (searchText.contains("accounting")) {
                    return ACCOUNTANCY;
                }
                for (Department department : values()) {
                    if (department.getDescription().toLowerCase().contains(searchText)) {
                        return department;
                    }
                }
            }
            return null;
        }

    }

}
