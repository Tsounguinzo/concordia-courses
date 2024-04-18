import re
from python.utils.utilities import normalize_text, longest_common_start, extract_ids_from_text


def find_common_ids(name_ids, summary_ids, course_id):
    """ Find common IDs, handle cases where one set might be empty and ensure results are always returned as a
    set."""
    common_ids = set()
    if not name_ids and not summary_ids:
        return common_ids  # Both sets are empty, return empty set
    elif not name_ids or not summary_ids:
        # One set is empty, use the non-empty set and check it against the normalized course ID
        active_set = name_ids if name_ids else summary_ids
        normalized_course_id = normalize_text(course_id)
        return {id for id in active_set if normalize_text(id).startswith(normalized_course_id)}
    else:
        # Both sets are non-empty, find common IDs
        for name_id in name_ids:
            for summary_id in summary_ids:
                # Check if normalized IDs match at least on the first four characters
                if normalize_text(name_id)[:4] == normalize_text(summary_id)[:4]:
                    # Calculate the longest common starting substring and add to the set
                    common_id = longest_common_start(name_id, summary_id)
                    if common_id:
                        common_ids.add(common_id)
        return common_ids


def validate_and_refine_ids(common_ids, valid_ids, course_id):
    """ Validate IDs by checking against valid_ids, with a fallback on course_id. """
    validated_ids = set()

    for cid in common_ids:
        prefix = re.match(r'([A-Z]+)', cid).group(1)
        if prefix in valid_ids and cid[len(prefix):] in valid_ids[prefix]:
            validated_ids.add(cid)
        elif cid.startswith(course_id):
            validated_ids.add(course_id)

    return validated_ids


def process_courses(course_data, valid_ids):
    processed_courses = {}
    used_ids = set()

    for course in course_data:
        course_name_ids = extract_ids_from_text(course['course_name'])
        summary_ids = extract_ids_from_text(course['summary'])
        common_ids = find_common_ids(course_name_ids, summary_ids, course['courseid'])
        combined_ids = validate_and_refine_ids(common_ids, valid_ids, course['courseid'])

        for course_id in combined_ids:
            if course_id not in processed_courses:
                processed_courses[course_id] = {
                    'courseId': course_id,
                    'teachers': set(),
                    'non_editing_teachers': set(),
                    'teaching_assistants': set()
                }

            # Aggregate all data for the same course ID
            processed_courses[course_id]['teachers'].update(course.get('teachers', []))
            processed_courses[course_id]['non_editing_teachers'].update(course.get('non_editing_teachers', []))
            processed_courses[course_id]['teaching_assistants'].update(course.get('teaching_assistants', []))

    # Convert the sets to lists for JSON serialization and remove duplicates
    final_courses = list(processed_courses.values())
    for course in final_courses:
        course['teachers'] = list(course['teachers'])
        course['non_editing_teachers'] = list(course['non_editing_teachers'])
        course['teaching_assistants'] = list(course['teaching_assistants'])

    return final_courses, used_ids
