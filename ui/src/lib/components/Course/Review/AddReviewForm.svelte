<script lang="ts">
    import Transition from "svelte-transition";
    import {twMerge} from "tailwind-merge";
    import {Form, Sveltik} from "sveltik";
    import {repo} from "$lib/repo.js";
    import ReviewForm from "$lib/components/Course/Review/ReviewForm.svelte";
    import {toast} from "svelte-sonner";
    import {darkModeOn} from "$lib/provider/darkmode";
    import type {Course} from "$lib/model/Course";

    export let course: Course;
  export let open: boolean;
  export let onClose: () => void;
  export let handleSubmit: (res: Response) => void;

  const initialValues = {
    content: '',
    instructors: [],
    rating: 0,
    difficulty: 0,
  };

  const handleClose = () => {
    onClose();
    toast.success('Review draft saved.');
  };
</script>

<Transition appear show={open}>
      <div class={twMerge('relative z-50', $darkModeOn ? 'dark' : '')}
        on:close={handleClose}
      >
        <Transition
          enter='ease-out duration-200'
          enterFrom='opacity-0'
          enterTo='opacity-100'
          leave='ease-in duration-200'
          leaveFrom='opacity-100'
          leaveTo='opacity-0'
        >
          <div class='fixed inset-0 bg-black/25' />
        </Transition>

        <div class='fixed inset-y-0 left-0 w-screen overflow-y-scroll'>
          <div class='flex min-h-full items-center justify-center p-4 text-center'>
            <Transition
              enter='ease-out duration-200'
              enterFrom='opacity-0 scale-95'
              enterTo='opacity-100 scale-100'
              leave='ease-in duration-150'
              leaveFrom='opacity-100 scale-100'
              leaveTo='opacity-0 scale-95'
            >
              <div class='w-[448px] overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all dark:bg-neutral-800' use:dialog.modal>
                <h3 class='mb-4 text-lg font-medium leading-6 text-gray-900 dark:text-gray-200'>
                  {`Reviewing ${course.subject} ${course.catalog} - ${course.title}`}
                </h3>
                <Sveltik
                  initialValues={initialValues}
                  validationSchema={ReviewSchema}
                  onSubmit={async (values, actions) => {
                    const res = await repo.addReview(course._id, values);
                    actions.setSubmitting(false);
                    onClose();
                    handleSubmit(res);
                  }}
                >
                  {({ values, setFieldValue, resetForm })}
                    <Form>
                      <ReviewForm
                        course={course}
                        values={values}
                        setFieldValue={setFieldValue}
                        resetForm={resetForm}
                      />
                    </Form>
                </Sveltik>
              </div>
            </Transition>
          </div>
        </div>
      </div>
    </Transition>