import argparse
import json
import os
from datetime import datetime
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Dict, List, Any, Tuple, Optional
from utils import load_json, parse_course_codes, parse_reviews, \
    find_best_department_match, parse_tags, save_json, update_reviews, \
    convert_timestamps

API_URL = "https://www.ratemyprofessors.com/graphql"
HEADERS = {
    "Content-Type": "application/json",
    "Authorization": "Basic dGVzdDp0ZXN0",
    "Origin": "https://www.ratemyprofessors.com",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
    "Sec-Fetch-Dest": "empty",
    "Sec-Fetch-Mode": "cors",
    "Sec-Fetch-Site": "same-origin",
    "Accept": "*/*",
    "Accept-Encoding": "gzip, deflate, br, zstd",
    "Accept-Language": "en-US,en;q=0.9",
    "Dnt": "1"
}


def fetch_professor_data(instructor_id):
    """
        Fetch detailed data for a professor by their ID.

        Args:
            instructor_id (str): The ID of the professor.

        Returns:
            Dict[str, Any]: The detailed data of the professor.
    """
    payload = {
        "query": "query TeacherRatingsPageQuery(\n  $id: ID!\n) {\n  node(id: $id) {\n    __typename\n    ... on Teacher {\n      id\n      legacyId\n      firstName\n      lastName\n      department\n      school {\n        legacyId\n        name\n        city\n        state\n        country\n        id\n      }\n      lockStatus\n      ...StickyHeader_teacher\n      ...RatingDistributionWrapper_teacher\n      ...TeacherInfo_teacher\n      ...SimilarProfessors_teacher\n      ...TeacherRatingTabs_teacher\n    }\n    id\n  }\n}\n\nfragment StickyHeader_teacher on Teacher {\n  ...HeaderDescription_teacher\n  ...HeaderRateButton_teacher\n}\n\nfragment RatingDistributionWrapper_teacher on Teacher {\n  ...NoRatingsArea_teacher\n  ratingsDistribution {\n    total\n    ...RatingDistributionChart_ratingsDistribution\n  }\n}\n\nfragment TeacherInfo_teacher on Teacher {\n  id\n  lastName\n  numRatings\n  ...RatingValue_teacher\n  ...NameTitle_teacher\n  ...TeacherTags_teacher\n  ...NameLink_teacher\n  ...TeacherFeedback_teacher\n  ...RateTeacherLink_teacher\n  ...CompareProfessorLink_teacher\n}\n\nfragment SimilarProfessors_teacher on Teacher {\n  department\n  relatedTeachers {\n    legacyId\n    ...SimilarProfessorListItem_teacher\n    id\n  }\n}\n\nfragment TeacherRatingTabs_teacher on Teacher {\n  numRatings\n  courseCodes {\n    courseName\n    courseCount\n  }\n  ...RatingsList_teacher\n  ...RatingsFilter_teacher\n}\n\nfragment RatingsList_teacher on Teacher {\n  id\n  legacyId\n  lastName\n  numRatings\n  school {\n    id\n    legacyId\n    name\n    city\n    state\n    avgRating\n    numRatings\n  }\n  ...Rating_teacher\n  ...NoRatingsArea_teacher\n  ratings(first: 1000000) {\n    edges {\n      cursor\n      node {\n        ...Rating_rating\n        id\n        __typename\n      }\n    }\n    pageInfo {\n      hasNextPage\n      endCursor\n    }\n  }\n}\n\nfragment RatingsFilter_teacher on Teacher {\n  courseCodes {\n    courseCount\n    courseName\n  }\n}\n\nfragment Rating_teacher on Teacher {\n  ...RatingFooter_teacher\n  ...RatingSuperHeader_teacher\n  ...ProfessorNoteSection_teacher\n}\n\nfragment NoRatingsArea_teacher on Teacher {\n  lastName\n  ...RateTeacherLink_teacher\n}\n\nfragment Rating_rating on Rating {\n  comment\n  flagStatus\n  createdByUser\n  teacherNote {\n    id\n  }\n  ...RatingHeader_rating\n  ...RatingSuperHeader_rating\n  ...RatingValues_rating\n  ...CourseMeta_rating\n  ...RatingTags_rating\n  ...RatingFooter_rating\n  ...ProfessorNoteSection_rating\n}\n\nfragment RatingHeader_rating on Rating {\n  legacyId\n  date\n  class\n  helpfulRating\n  clarityRating\n  isForOnlineClass\n}\n\nfragment RatingSuperHeader_rating on Rating {\n  legacyId\n}\n\nfragment RatingValues_rating on Rating {\n  helpfulRating\n  clarityRating\n  difficultyRating\n}\n\nfragment CourseMeta_rating on Rating {\n  attendanceMandatory\n  wouldTakeAgain\n  grade\n  textbookUse\n  isForOnlineClass\n  isForCredit\n}\n\nfragment RatingTags_rating on Rating {\n  ratingTags\n}\n\nfragment RatingFooter_rating on Rating {\n  id\n  comment\n  adminReviewedAt\n  flagStatus\n  legacyId\n  thumbsUpTotal\n  thumbsDownTotal\n  thumbs {\n    thumbsUp\n    thumbsDown\n    computerId\n    id\n  }\n  teacherNote {\n    id\n  }\n  ...Thumbs_rating\n}\n\nfragment ProfessorNoteSection_rating on Rating {\n  teacherNote {\n    ...ProfessorNote_note\n    id\n  }\n  ...ProfessorNoteEditor_rating\n}\n\nfragment ProfessorNote_note on TeacherNotes {\n  comment\n  ...ProfessorNoteHeader_note\n  ...ProfessorNoteFooter_note\n}\n\nfragment ProfessorNoteEditor_rating on Rating {\n  id\n  legacyId\n  class\n  teacherNote {\n    id\n    teacherId\n    comment\n  }\n}\n\nfragment ProfessorNoteHeader_note on TeacherNotes {\n  createdAt\n  updatedAt\n}\n\nfragment ProfessorNoteFooter_note on TeacherNotes {\n  legacyId\n  flagStatus\n}\n\nfragment Thumbs_rating on Rating {\n  id\n  comment\n  adminReviewedAt\n  flagStatus\n  legacyId\n  thumbsUpTotal\n  thumbsDownTotal\n  thumbs {\n    computerId\n    thumbsUp\n    thumbsDown\n    id\n  }\n  teacherNote {\n    id\n  }\n}\n\nfragment RateTeacherLink_teacher on Teacher {\n  legacyId\n  numRatings\n  lockStatus\n}\n\nfragment RatingFooter_teacher on Teacher {\n  id\n  legacyId\n  lockStatus\n  isProfCurrentUser\n  ...Thumbs_teacher\n}\n\nfragment RatingSuperHeader_teacher on Teacher {\n  firstName\n  lastName\n  legacyId\n  school {\n    name\n    id\n  }\n}\n\nfragment ProfessorNoteSection_teacher on Teacher {\n  ...ProfessorNote_teacher\n  ...ProfessorNoteEditor_teacher\n}\n\nfragment ProfessorNote_teacher on Teacher {\n  ...ProfessorNoteHeader_teacher\n  ...ProfessorNoteFooter_teacher\n}\n\nfragment ProfessorNoteEditor_teacher on Teacher {\n  id\n}\n\nfragment ProfessorNoteHeader_teacher on Teacher {\n  lastName\n}\n\nfragment ProfessorNoteFooter_teacher on Teacher {\n  legacyId\n  isProfCurrentUser\n}\n\nfragment Thumbs_teacher on Teacher {\n  id\n  legacyId\n  lockStatus\n  isProfCurrentUser\n}\n\nfragment SimilarProfessorListItem_teacher on RelatedTeacher {\n  legacyId\n  firstName\n  lastName\n  avgRating\n}\n\nfragment RatingValue_teacher on Teacher {\n  avgRating\n  numRatings\n  ...NumRatingsLink_teacher\n}\n\nfragment NameTitle_teacher on Teacher {\n  id\n  firstName\n  lastName\n  department\n  school {\n    legacyId\n    name\n    id\n  }\n  ...TeacherDepartment_teacher\n  ...TeacherBookmark_teacher\n}\n\nfragment TeacherTags_teacher on Teacher {\n  lastName\n  teacherRatingTags {\n    legacyId\n    tagCount\n    tagName\n    id\n  }\n}\n\nfragment NameLink_teacher on Teacher {\n  isProfCurrentUser\n  id\n  legacyId\n  firstName\n  lastName\n  school {\n    name\n    id\n  }\n}\n\nfragment TeacherFeedback_teacher on Teacher {\n  numRatings\n  avgDifficulty\n  wouldTakeAgainPercent\n}\n\nfragment CompareProfessorLink_teacher on Teacher {\n  legacyId\n}\n\nfragment TeacherDepartment_teacher on Teacher {\n  department\n  departmentId\n  school {\n    legacyId\n    name\n    id\n  }\n}\n\nfragment TeacherBookmark_teacher on Teacher {\n  id\n  isSaved\n}\n\nfragment NumRatingsLink_teacher on Teacher {\n  numRatings\n  ...RateTeacherLink_teacher\n}\n\nfragment RatingDistributionChart_ratingsDistribution on ratingsDistribution {\n  r1\n  r2\n  r3\n  r4\n  r5\n}\n\nfragment HeaderDescription_teacher on Teacher {\n  id\n  firstName\n  lastName\n  department\n  school {\n    legacyId\n    name\n    city\n    state\n    id\n  }\n  ...TeacherTitles_teacher\n  ...TeacherBookmark_teacher\n}\n\nfragment HeaderRateButton_teacher on Teacher {\n  ...RateTeacherLink_teacher\n}\n\nfragment TeacherTitles_teacher on Teacher {\n  department\n  school {\n    legacyId\n    name\n    id\n  }\n}\n",
        "variables": {"id": instructor_id}
    }

    response = requests.post(API_URL, headers=HEADERS, json=payload)
    return response.json()


def process_professor(edge: Dict[str, Any]) -> Dict[str, Any]:
    """
        Process professor data to fetch detailed information.

        Args:
            edge (Dict[str, Any]): The professor edge containing basic information.

        Returns:
            Dict[str, Any]: A dictionary with the professor ID and their detailed data.
    """
    professor_id = edge['node']['id']
    detailed_info = fetch_professor_data(professor_id)
    return {"id": professor_id, "data": detailed_info['data']}


def fetch_all_professor_data(data: Dict[str, Any]) -> List[Dict[str, Any]]:
    """
        Fetch detailed data for all professors listed in the initial data.

        Args:
            data (Dict[str, Any]): The initial data containing professor edges.

        Returns:
            List[Dict[str, Any]]: A list of dictionaries with detailed professor data.
    """
    professors = data['data']['search']['teachers']['edges']
    all_professors_details = []

    with ThreadPoolExecutor() as executor:
        futures = [executor.submit(process_professor, edge) for edge in professors]
        for future in as_completed(futures):
            all_professors_details.append(future.result())

    return all_professors_details


def parse_instructor(node: Dict[str, Any], catalog_lookup: Dict[str, List[str]], departments: List[str]) -> Dict[str, Any]:
    """
        Parse the instructor data into a structured format.

        Args:
            node (Dict[str, Any]): The raw instructor data.
            catalog_lookup (Dict[str, List[str]]): A lookup dictionary for course codes.
            departments (List[str]): A list of department names.

        Returns:
            Dict[str, Any]: A dictionary containing parsed instructor information.
    """
    instructor_id = '-'.join(f"{node['firstName']} {node['lastName']}".split()).lower()
    courses = parse_course_codes(node['courseCodes'], catalog_lookup)
    return {
        "_id": instructor_id,
        "firstName": node['firstName'],
        "lastName": node['lastName'],
        "departments": [find_best_department_match(node['department'], departments)],
        "courses": courses,
        "tags": list(parse_tags(node['teacherRatingTags'])),
        "avgDifficulty": node['avgDifficulty'],
        "avgRating": node['avgRating'],
        "reviewCount": node['numRatings']
    }


def process_item(item: Dict[str, Any], catalog_lookup: Dict[str, List[str]], departments: List[str], cutoff_date: datetime) -> Tuple[Optional[Dict[str, Any]], List[Dict[str, Any]]]:
    """
        Process a single item of data to extract instructor and reviews information.

        Args:
            item (Dict[str, Any]): The raw data item.
            catalog_lookup (Dict[str, List[str]]): A lookup dictionary for course codes.
            departments (List[str]): A list of department names.
            cutoff_date (datetime): The cutoff date for filtering reviews.

        Returns:
            Tuple[Optional[Dict[str, Any]], List[Dict[str, Any]]]: A tuple containing the instructor data and a list of reviews.
    """
    node = item['data']['node']
    if not node:
        return None, []
    instructor = parse_instructor(node, catalog_lookup, departments)
    reviews = parse_reviews(node['ratings']['edges'], instructor['courses'], instructor['_id'], cutoff_date)
    return instructor, reviews


def main(cutoff_date: str):
    cutoff_date = datetime.strptime(cutoff_date, '%Y-%m-%d')
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Step 1: Fetch professor data
    initial_data_path = os.path.join(script_dir, 'concordiaProfRateMyProf.json')
    initial_data = load_json(initial_data_path)
    data = fetch_all_professor_data(initial_data)

    # Step 2: Process fetched data
    catalog_lookup_path = os.path.join(script_dir, 'subject-catalogs.json')
    departments_path = os.path.join(script_dir, 'department_descriptions.json')

    catalog_lookup = load_json(catalog_lookup_path)
    departments = load_json(departments_path)

    instructors = []
    reviews = []

    with ThreadPoolExecutor() as executor:
        futures = [executor.submit(process_item, item, catalog_lookup, departments, cutoff_date) for item in data]
        for future in as_completed(futures):
            instructor, item_reviews = future.result()
            if instructor:
                instructors.append(instructor)
                reviews.extend(item_reviews)

    # Step 3: Update reviews
    instructor_lists_path = os.path.join(script_dir, 'updated_ids.json')
    instructor_lists = load_json(instructor_lists_path)
    update_reviews(reviews, instructor_lists)

    # Step 4: Convert timestamps
    convert_timestamps(reviews)

    # Print JSON result to stdout
    print(json.dumps(reviews))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Process professor reviews with a cutoff date.")
    parser.add_argument("cutoff_date", help="Cutoff date in YYYY-MM-DD format")
    args = parser.parse_args()

    main(args.cutoff_date)
