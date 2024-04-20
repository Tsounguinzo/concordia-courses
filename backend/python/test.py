import json


def read_json_file(file_path):
    with open(file_path, 'r') as file:
        return json.load(file)


def get_teacher_names(data, keys=None):
    if keys is None:
        keys = ['teachers', 'non_editing_teachers', 'teaching_assistants']
    names = set()
    for entry in data:
        for key in keys:
            if key in entry:
                names.update(entry[key])
    return names


def compare_names(file1_path, file2_path):
    # Read data from files
    data1 = read_json_file(file1_path)
    data2 = read_json_file(file2_path)

    # Get names from each file
    names1 = get_teacher_names(data1)
    names2 = get_teacher_names(data2)

    # Find unique names and where they are found
    unique_to_file1 = names1 - names2
    unique_to_file2 = names2 - names1

    return {
        'unique_to_file1': unique_to_file1,
        'unique_to_file2': unique_to_file2
    }


if __name__ == '__main__':
    file1_path = 'data/output/all_courses.json'
    file2_path = 'data/output/processed_courses.json'
    result = compare_names(file1_path, file2_path)
    unsearchable_result = {'Meredith Marty-Dugas', 'Jill Kinaschuk', 'Jessica Krejcik', 'Lian Duan', 'STEFANIE CORONA', 'PARISSA ZOHARI', 'DONNA WHITTAKER', 'Ali Albashext', 'CASSANDRA LAMONTAGNE', 'SILVANA NOVEMBRE', 'Nancy Di Girolamo', 'JILL KINASCHUCK', 'PIETRO GASPARRINI', 'CHRISTINA ST. LOUIS-TULLI'}
    print(f"Unique to file 1:({len(result['unique_to_file1'] - unsearchable_result)})", result['unique_to_file1'] - unsearchable_result)
    print(f"Unique to file 2:({len(result['unique_to_file2'])})", result['unique_to_file2'])
