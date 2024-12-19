<script lang="ts">
    import Form from "$lib/components/common/form/Form.svelte";
    import {Field, Sveltik} from "sveltik/src";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import Submit from "$lib/components/common/form/Submit.svelte";
    import {toast} from "svelte-sonner";
    import {
        validateYear,
        validateFile,
        type FileValidationOptions,
        validateFromOptions
    } from "$lib/validators.js";
    import AutocompleteInput from "$lib/components/review/AutocompleteInput.svelte";
    import FileInput from "$lib/components/common/form/FileInput.svelte";
    import ExampleDialog from "$lib/components/grades/ExampleDialog.svelte";
    import { uploadFiles } from "$lib/utils/uploadthing";
    import {type Writable} from "svelte/store";

    let initialValues = {
        term: '',
        year: '',
        gradeDistribution: null,
    };

    let reset: boolean = false;
    export let triggerConfetti: Writable<boolean>

    let gradeDistributionExample = {
        title: 'Getting your Grade Distribution',
       content: `
       <h3 class="text-base">Instructions</h3>
       <p class="text-sm ">
           Step 1: Log in to the student portal.<br>
           Step 2: Navigate to the 'View My Grades'.<br>
           Step 3: Choose the term.<br>
           Step 4: Click on the dropdown button.<br>
           Step 5: Choose 'Grade Distribution'.<br>
           Step 6: Take a screenshot as shown below.<br>
       </p>
       `,
        image: '/grades.png'
    };

    const validate = (values, actions) => {
        const errors = {
            term: '',
            year: '',
            gradeDistribution: '',
        };

        const options: FileValidationOptions = {
            allowedTypes: ["image/jpeg", "image/png"],
            maxSizeMB: 4,
        };
        errors.term = validateFromOptions(values.term, "Term", ['Fall', 'Winter', 'Spring', 'Summer', 'Fall/Winter']);
        errors.year = validateYear(values.year);
        errors.gradeDistribution = validateFile(values.gradeDistribution, "Grade Distribution", options);

        return errors;
    };

    const handleFileChange = (file, setFieldValue, field) => {
        if (file) {
            setFieldValue(field, file);
        }
    };

    const onSubmit = async (values, actions) => {
        try {
            const { term, year, gradeDistribution } = values;

            // Create a new file name
            const newFileName = `${term}-${year}-gradeDistribution${gradeDistribution.name.slice(gradeDistribution.name.lastIndexOf('.'))}`;

            // Create a new File object with the new name
            const renamedFile = new File([gradeDistribution], newFileName, {
                type: gradeDistribution.type,
                lastModified: gradeDistribution.lastModified,
            });


            // Start upload with UploadThing
            toast.promise(uploadFiles(
                'gradeDistribution',
                {
                    files: [renamedFile],
                }
            ), {
                loading: 'Uploading file...',
                success: () => {
                   triggerConfetti.set(true);
                   return 'Uploaded successfully.'
                },
                error: (err) => err.message || 'Upload failed'
            });

            actions.resetForm();
            reset = true;
            setTimeout(() => reset = false, 100);
        } catch (error) {
            toast.error(error.message || 'Upload failed');
        } finally {
            actions.setSubmitting(false);
        }
    };

</script>

<Sveltik
        validateOnBlur={false}
        validateOnChange={false}
        {validate}
        {initialValues}
        onSubmit={onSubmit}
        let:props
        let:setFieldValue
        let:isSubmitting
>
    <Form storageKey="grades-form">
        <div style="max-height: calc(100vh - 10rem);" class='overflow-auto scrollbar-hide'>
            <div class="flex gap-x-3 w-full max-sm:flex-col">
                <div class='flex flex-col flex-grow'>
                    <FieldLabel For='term'>Term</FieldLabel>
                    <AutocompleteInput
                            {setFieldValue}
                            {reset}
                            value={props.values.term}
                            options={['Fall', 'Winter', 'Spring', 'Summer', 'Fall/Winter']}
                            name='term'
                            placeholder='e.g., Fall'
                            class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white w-full'
                    />
                    <FieldError name='term'/>
                </div>
                <div class='flex flex-col flex-grow'>
                    <FieldLabel For='year'>Year</FieldLabel>
                    <Field
                            on:input={(e) => props.values.year = e.target.value}
                            on:blur={props.handleBlur}
                            value={props.values.year}
                            id='year'
                            name='year'
                            placeholder='e.g., 2023'
                            class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                    />
                    <FieldError name='year'/>
                </div>
            </div>
            <div class='flex flex-col'>
                <div class="flex gap-x-3">
                    <FieldLabel For='gradeDistribution'>
                        Grade Distribution
                    </FieldLabel>
                    <ExampleDialog {...gradeDistributionExample}/>
                </div>
                <FileInput
                        id="gradeDistribution"
                        name="gradeDistribution"
                        accept="image/jpeg,image/png,application/pdf"
                        class='capitalize resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                        file={props.values.gradeDistribution}
                        onChange={(file) => handleFileChange(file, setFieldValue, 'gradeDistribution')}
                />
                <FieldError name='gradeDistribution'/>
            </div>
            <div class='mt-8 flex justify-end space-x-4'>
                <button type="reset"
                        class='w-fit cursor-pointer rounded-md bg-gray-100 px-4 py-2 font-medium text-gray-700 duration-200 hover:bg-gray-200 dark:bg-neutral-700 dark:text-gray-200 dark:hover:bg-neutral-600'
                >
                    Reset
                </button>
                <Submit {isSubmitting}/>
            </div>
        </div>
    </Form>
</Sveltik>