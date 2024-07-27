"""
Utility functions for processing professor and course data.

This module contains functions for loading and saving JSON data, parsing reviews,
matching departments, and handling course codes and tags.
"""

import json
import re
import uuid
from datetime import datetime
from typing import Any, Optional, Dict, List, Set


def load_json(filepath: str):
    """
        Load JSON data from a file.

        Args:
            filepath (str): Path to the JSON file.

        Returns:
            Any: Parsed JSON data.

        Raises:
            JSONDecodeError: If the file contains invalid JSON.
            FileNotFoundError: If the file doesn't exist.
        """
    with open(filepath, 'r') as file:
        return json.load(file)


def save_json(filepath: str, data: Any):
    """
        Save data as JSON to a file.

        Args:
            filepath (str): Path to save the JSON file.
            data (Any): Data to be saved as JSON.

        Raises:
            IOError: If there's an error writing to the file.
        """
    with open(filepath, 'w') as file:
        return json.dump(data, file, indent=4)


def find_best_department_match(input_text: str, departments: List[str]) -> Optional[str]:
    """
    Find the best matching department for a given input text.

    Args:
        input_text (str): The input text to match against departments.
        departments (list): A list of department names.

    Returns:
        Optional[str]: The best matching department name, or None if no match found.
    """
    if not input_text:
        return None

    # Direct mappings for specific cases
    direct_mappings = {
        'Accounting': 'Accountancy',
        'Theater': 'Theatre',
        'Theology': 'Theological Studies',
        'Religion': 'Religions and Cultures',
        'Exercise & Sport Science': 'Exercise Science',
        'Film': 'Fine Arts',
        'Law': 'Business Administration'
    }

    if input_text in direct_mappings:
        return direct_mappings[input_text]

    if input_text in {'Languages', 'Chinese', 'Linguistics', 'Arabic', 'Greek', 'Italian', 'German', 'Hebrew', 'Russian'}:
        return "Classics, Mod Lang&Linguistics"

    # Normalize input text and split into tokens
    input_tokens = set(re.sub(r'[^a-z0-9\s]', '', input_text.lower()).split())

    best_match = None
    highest_match_count = 0
    smallest_token_difference = float('inf')

    for department in departments:
        desc_tokens = set(re.sub(r'[^a-z0-9\s]', '', department.lower()).split())
        match_count = len(input_tokens.intersection(desc_tokens))
        token_difference = abs(len(desc_tokens) - len(input_tokens))

        if (match_count > highest_match_count or
                (match_count == highest_match_count and token_difference < smallest_token_difference)):
            best_match = department
            highest_match_count = match_count
            smallest_token_difference = token_difference

    return best_match


def parse_reviews(reviews: List[Dict[str, Any]], instructor_courses: List[Dict[str, str]],
                  instructor_id: str, cutoff_date: datetime) -> List[Dict[str, Any]]:
    """
    Parse and filter reviews based on instructor courses and cutoff date.

    Args:
        reviews (List[Dict[str, Any]]): List of review data.
        instructor_courses (List[Dict[str, str]]): List of instructor's courses.
        instructor_id (str): ID of the instructor.
        cutoff_date (datetime): Cutoff date for filtering reviews.

    Returns:
        List[Dict[str, Any]]: Parsed and filtered reviews.
    """
    parsed_reviews = []
    course_identifiers = {f"{course['subject']}{course['catalog']}".lower() for course in instructor_courses}

    for review in reviews:
        node = review['node']
        review_class = node['class'].replace(' ', '').lower()
        review_date = datetime.strptime(str(node['date']).split()[0], "%Y-%m-%d")

        if review_class in course_identifiers and review_date >= cutoff_date:
            review_data = {
                "type": "instructor",
                "content": node['comment'],
                "timestamp": node['date'],
                "adminReviewedAt": node['adminReviewedAt'],
                "flagged": node['flagStatus'] != "UNFLAGGED",
                "likes": node['thumbsUpTotal'] - node['thumbsDownTotal'],
                "userId": None,
                "difficulty": node['difficultyRating'],
                "courseId": review_class.upper(),
                "instructorId": instructor_id,
                "rating": node['clarityRating'],
                "tags": list(set(tag.strip() for tag in node['ratingTags'].split('--'))) if node['ratingTags'] else []
            }
            parsed_reviews.append(review_data)

    return parsed_reviews


def parse_course_codes(course_codes: List[Dict[str, str]], catalog_lookup: Dict[str, List[str]]) -> List[Dict[str, str]]:
    """
    Parse course codes and match them with catalog lookup.

    Args:
        course_codes (List[Dict[str, str]]): List of course codes.
        catalog_lookup (Dict[str, List[str]]): Catalog lookup dictionary.

    Returns:
        List[Dict[str, str]]: Parsed course codes.
    """
    courses = []
    for code in course_codes:
        course_name = code['courseName'].lower()
        for subject, catalogs in catalog_lookup.items():
            if course_name.startswith(subject.lower()):
                for catalog in catalogs:
                    if course_name == f"{subject}{catalog}".lower() or course_name.startswith(f"{subject}{catalog}".lower()):
                        courses.append({"subject": subject.upper(), "catalog": catalog.upper()})
                        break
                if courses and courses[-1]["subject"] == subject.upper():
                    break
    return courses


def parse_tags(teacher_tags: List[Dict[str, Any]]) -> Set[str]:
    """
    Parse teacher tags into a set of strings.

    Args:
        teacher_tags (List[Dict[str, Any]]): List of teacher tag dictionaries.

    Returns:
        Set[str]: Set of parsed tag names.
    """
    return {tag['tagName'] for tag in teacher_tags}

def find_longest_instructor_id(instructor_id: str, instructor_lists: List[List[str]]) -> str:
    """
    Find the longest instructor ID that contains the given instructor ID.

    Args:
        instructor_id (str): The instructor ID to match.
        instructor_lists (List[List[str]]): Lists of instructor IDs.

    Returns:
        str: The longest matching instructor ID.
    """
    matches = [ids for ids in instructor_lists if any(instructor_id in s for s in ids)]

    if len(matches) > 1:
        print(f"Ambiguity found for instructorId '{instructor_id}'. Multiple matches: {matches}")
        return instructor_id

    return max(matches[0], key=len) if matches else instructor_id


def update_reviews(reviews: List[Dict[str, Any]], instructor_lists: List[List[str]]) -> None:
    """
    Update userId and instructorId in reviews.

    Args:
        reviews (List[Dict[str, Any]]): List of reviews to update.
        instructor_lists (List[List[str]]): Lists of instructor IDs.
    """
    for review in reviews:
        review['userId'] = f"rate_my_professor_{uuid.uuid4()}"
        review['instructorId'] = find_longest_instructor_id(review['instructorId'], instructor_lists)


def parse_date(date_str: Optional[str]) -> Optional[datetime]:
    """
    Parse a date string into a datetime object.

    Args:
        date_str (Optional[str]): Date string to parse.

    Returns:
        Optional[datetime]: Parsed datetime object or None if parsing fails.
    """
    if not date_str:
        return None

    date_formats = ['%Y-%m-%d', '%Y-%m-%dT%H:%M:%S', '%Y-%m-%d %H:%M:%S %z UTC']

    for format in date_formats:
        try:
            return datetime.strptime(date_str, format)
        except ValueError:
            continue

    return None


def convert_timestamps(reviews: List[Dict[str, Any]]) -> None:
    """
    Convert timestamp formats in review records.

    Args:
        reviews (List[Dict[str, Any]]): List of reviews to update.
    """
    for review in reviews:
        for field in ['timestamp', 'adminReviewedAt']:
            if field in review and review[field]:
                parsed_date = parse_date(review[field])
                if parsed_date:
                    review[field] = parsed_date.isoformat(timespec='milliseconds')
