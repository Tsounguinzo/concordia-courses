import requests
import logging
from bs4 import BeautifulSoup


def fetch_html_content(url):
    """Fetches HTML content from a specified URL."""
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        return response.text
    except requests.RequestException as e:
        logging.error(f"Failed to retrieve webpage: {e}")
        return None


def parse_course_data(html_content, course_key, course_code):
    """Parses HTML content and returns course data."""
    course_details = []
    if not html_content:
        logging.error("No HTML content provided.")
        return course_details

    soup = BeautifulSoup(html_content, 'html.parser')
    course_boxes = soup.find_all('div', class_='coursebox')

    for box in course_boxes:
        try:
            course_info = box.find('div', class_='info')
            course_name = course_info.find('h3', class_='coursename').get_text(strip=True)
            course_summary = box.find('div', class_='summary').get_text(strip=True)

            teachers = []
            non_editing_teachers = []
            teaching_assistants = []

            for li in box.find_all('li'):
                role_text = li.find('span').text.strip()
                if 'Teacher:' in role_text:
                    teachers.append(li.find('a').get_text(strip=True))
                elif 'Teaching Assistant:' in role_text:
                    teaching_assistants.append(li.find('a').get_text(strip=True))
                elif 'Non-editing teacher:' in role_text:
                    non_editing_teachers.append(li.find('a').get_text(strip=True))

            course_dict = {
                'courseid': f"{course_key}{course_code}",
                'course_name': course_name,
                'summary': course_summary,
                'teachers': teachers,
                'non_editing_teachers': non_editing_teachers,
                'teaching_assistants': teaching_assistants
            }
            print(course_dict)
            course_details.append(course_dict)
        except Exception as e:
            logging.warning(f"Error parsing course box: {str(e)}")

    return course_details