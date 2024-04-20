import logging
import os
from tqdm import tqdm

from python.processor.processor import process_courses
from python.scraper.scraper import fetch_html_content, parse_course_data
from python.utils.utilities import load_json, check_missing_ids, save_json

# Configuration
CONFIG = {
    "json_filepath": "data",
    "courses_url_template": "https://moodle.concordia.ca/moodle/course/search.php?areaids=core_course-course&q={}",
    "log_file": "data/logs/application.log"
}


def setup_logging():
    if not os.path.exists(os.path.dirname(CONFIG['log_file'])):
        os.makedirs(os.path.dirname(CONFIG['log_file']))
    logging.basicConfig(filename=CONFIG['log_file'], level=logging.INFO,
                        format='%(asctime)s - %(levelname)s - %(message)s')


def save_course_data(all_course_data, failed_retrievals):
    output_path = os.path.join(CONFIG['json_filepath'], 'output')
    json_filename = os.path.join(output_path, 'all_courses.json')
    failures_filename = os.path.join(output_path, 'failed_retrievals.json')

    try:
        save_json(all_course_data, json_filename)
        save_json(failed_retrievals, failures_filename)
    except Exception as e:
        logging.error(f"Error saving data: {e}")
        raise


def fetch_and_parse_courses(courses):
    all_course_data = []
    failed_retrievals = []

    # Calculate the total number of courses for the progress bar
    total_courses = sum(len(codes) for codes in courses.values())
    progress_bar = tqdm(total=total_courses, desc="Fetching Courses", unit="course")

    for course_key, course_codes in courses.items():
        for course_code in course_codes:
            url = CONFIG['courses_url_template'].format(course_key + '+' + course_code)
            try:
                html_content = fetch_html_content(url)
                if html_content:
                    course_data = parse_course_data(html_content, course_key, course_code)
                    all_course_data.extend(course_data or [])
                else:
                    failed_retrievals.append(f"{course_key}{course_code}")
            except Exception as e:
                logging.error(f"Failed to process {course_key} {course_code}: {e}")
                failed_retrievals.append(f"{course_key}{course_code}")
            finally:
                # Update progress bar after each course is processed
                progress_bar.update(1)

    progress_bar.close()
    return all_course_data, failed_retrievals


def main():
    setup_logging()
    logging.info("Starting the course data processing workflow.")

    try:
        json_valid_ids = load_json(os.path.join(CONFIG['json_filepath'], 'input', 'subject-catalogs.json'))
        #all_course_data, failed_retrievals = fetch_and_parse_courses(json_valid_ids)
        #save_course_data(all_course_data, failed_retrievals)
        all_course_data = load_json(os.path.join(CONFIG['json_filepath'], 'output', 'all_courses.json'))
        failed_retrievals = load_json(os.path.join(CONFIG['json_filepath'], 'output', 'failed_retrievals.json'))

        final_courses, used_ids = process_courses(all_course_data, json_valid_ids)

        check_missing_ids(json_valid_ids, used_ids, failed_retrievals)
        save_json(final_courses, os.path.join(CONFIG['json_filepath'], 'output', 'processed_courses.json'))
        logging.info("Course data processing and saving completed successfully.")
    except Exception as e:
        logging.error(f"An error occurred in the process: {e}")


if __name__ == "__main__":
    main()
